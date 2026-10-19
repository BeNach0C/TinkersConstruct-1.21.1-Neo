package slimeknights.tconstruct.tables.block.entity.table;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.inventory.CraftingContainerWrapper;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer.ILazyCrafter;
import slimeknights.tconstruct.tables.menu.CraftingStationContainerMenu;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;

import javax.annotation.Nullable;
import java.util.Collections;

public class CraftingStationBlockEntity extends RetexturedTableBlockEntity implements ILazyCrafter {
  public static final Component UNCRAFTABLE = TConstruct.makeTranslation("gui", "crafting_station.uncraftable");
  private static final Component NAME = TConstruct.makeTranslation("gui", "crafting_station");

  /** Last crafted crafting recipe */
  @Nullable
  private net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> lastRecipe;
  public CraftingRecipe getLastRecipe() { return lastRecipe == null ? null : lastRecipe.value(); }
  /** Result inventory, lazy loads results */
  @Getter
  private final LazyResultContainer craftingResult;
  /** Crafting inventory for the recipe calls */
  private final CraftingContainerWrapper craftingInventory;

  public CraftingStationBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerTables.craftingStationTile.get(), pos, state, NAME, 9);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
    this.itemHandlerCap = this.itemHandler;
    this.craftingInventory = new CraftingContainerWrapper(this, 3, 3);
    this.craftingResult = new LazyResultContainer(this);
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int menuId, Inventory playerInventory, Player playerEntity) {
    return new CraftingStationContainerMenu(menuId, playerInventory, this);
  }

  @Override
  public AABB getRenderBoundingBox() {
    return AABB.encapsulatingFullBlocks(worldPosition, worldPosition.offset(1, 2, 1));
  }

  /* Crafting */

  @Override
  public ItemStack calcResult(@Nullable Player player) {
    if (this.level == null || isEmpty()) {
      return ItemStack.EMPTY;
    }
    // assume empty unless we learn otherwise
    ItemStack result = ItemStack.EMPTY;
    if (!this.level.isClientSide && this.level.getServer() != null) {
      RecipeManager manager = this.level.getServer().getRecipeManager();

      // first, try the cached recipe
      CommonHooks.setCraftingPlayer(player);
      net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> recipeHolder = lastRecipe;
      // if it does not match, find a new recipe
      // note we intentionally have no player access during matches, that could lead to an unstable recipe
      if (recipeHolder == null || !recipeHolder.value().matches(this.craftingInventory.asCraftInput(), this.level)) {
        recipeHolder = manager.getRecipeFor(RecipeType.CRAFTING, this.craftingInventory.asCraftInput(), this.level).orElse(null);
      }

      // if we have a recipe, fetch its result
      if (recipeHolder != null) {
        CraftingRecipe recipe = recipeHolder.value();
        result = recipe.assemble(this.craftingInventory.asCraftInput(), level.registryAccess());

        // sync if the recipe is different
        if (recipeHolder != lastRecipe) {
          this.lastRecipe = recipeHolder;
          this.syncToRelevantPlayers(this::syncRecipe);
        }
      }
      CommonHooks.setCraftingPlayer(null);
    }
    else if (this.lastRecipe != null && this.lastRecipe.value().matches(this.craftingInventory.asCraftInput(), this.level)) {
      CommonHooks.setCraftingPlayer(player);
      result = this.lastRecipe.value().assemble(this.craftingInventory.asCraftInput(), level.registryAccess());
      CommonHooks.setCraftingPlayer(null);
    }
    return result;
  }

  /**
   * Gets the player sensitive crafting result, also validating the player has access to this recipe
   * @param player  Player
   * @return  Player sensitive result
   */
  public ItemStack getResultForPlayer(Player player) {
    CommonHooks.setCraftingPlayer(player);
    net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> recipeHolder = this.lastRecipe; // local variable just to prevent race conditions if the field changes, though that is unlikely

    // try matches again now that we have player access
    if (recipeHolder == null || this.level == null || !recipeHolder.value().matches(craftingInventory.asCraftInput(), level)) {
      CommonHooks.setCraftingPlayer(null);
      return ItemStack.EMPTY;
    }

    ItemStack result = recipeHolder.value().assemble(craftingInventory.asCraftInput(), level.registryAccess());
    CommonHooks.setCraftingPlayer(null);
    return result;
  }

  /**
   * Removes the result from this inventory, updating inputs and triggering recipe hooks
   * @param player  Player taking result
   * @param result  Result removed
   * @param amount  Number of times crafted
   */
  public void takeResult(Player player, ItemStack result, int amount) {
    net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> recipeHolder = this.lastRecipe; // local variable just to prevent race conditions if the field changes, though that is unlikely
    if (recipeHolder == null || this.level == null) {
      return;
    }
    CraftingRecipe recipe = recipeHolder.value();

    // fire crafting events
    if (!recipe.isSpecial()) {
      // unlock the recipe if it was not unlocked, so it shows in the recipe book
      player.awardRecipes(Collections.singleton(recipeHolder));
    }
    result.onCraftedBy(this.level, player, amount);
    EventHooks.firePlayerCraftingEvent(player, result, this.craftingInventory);

    // update all slots in the inventory
    // remove remaining items
    net.minecraft.world.item.crafting.CraftingInput.Positioned positioned = this.craftingInventory.asPositionedCraftInput();
    net.minecraft.world.item.crafting.CraftingInput input = positioned.input();
    int left = positioned.left();
    int top = positioned.top();

    CommonHooks.setCraftingPlayer(player);
    NonNullList<ItemStack> remaining = recipe.getRemainingItems(input);
    CommonHooks.setCraftingPlayer(null);

    for (int row = 0; row < input.height(); ++row) {
      for (int col = 0; col < input.width(); ++col) {
        int slotIndex = (col + left) + (row + top) * this.craftingInventory.getWidth();
        ItemStack original = this.getItem(slotIndex);
        ItemStack newStack = remaining.get(col + row * input.width());

        if (!original.isEmpty()) {
          this.removeItem(slotIndex, 1);
          original = this.getItem(slotIndex);
        }

        if (!newStack.isEmpty()) {
          if (original.isEmpty()) {
            this.setItem(slotIndex, newStack);
          } else if (ItemStack.isSameItemSameComponents(original, newStack)) {
            newStack.grow(original.getCount());
            this.setItem(slotIndex, newStack);
          } else if (!player.getInventory().add(newStack)) {
            player.drop(newStack, false);
          }
        }
      }
    }
  }

  /** Sends a message alerting the player this item is currently uncraftable, typically due to gamerules */
  public void notifyUncraftable(Player player) {
    // if empty, send a message so the player is more aware of why they cannot craft it, sent to chat as status bar is not visible
    // TODO: consider moving into the UI somewhere
    if (level != null && !level.isClientSide) {
      player.displayClientMessage(CraftingStationBlockEntity.UNCRAFTABLE, false);
    }
  }

  @Override
  public void onCraft(Player player, ItemStack result, int amount) {
    // update the inputs and trigger recipe hooks
    if (amount != 0 && !result.isEmpty()) {
      takeResult(player, result, amount);
    }
  }

  @Override
  public void setItem(int slot, ItemStack itemstack) {
    super.setItem(slot, itemstack);
    // clear the crafting result when the matrix changes so we recalculate the result
    this.craftingResult.clearContent();
  }


  /* Syncing */

  /**
   * Sends the current recipe to the given player
   * @param player  Player to send an update to
   */
  public void syncRecipe(Player player) {
    // must have a last recipe and a server world
    if (this.lastRecipe != null && this.level != null && !this.level.isClientSide && player instanceof ServerPlayer) {
      TinkerNetwork.getInstance().sendTo(new UpdateCraftingRecipePacket(this.worldPosition, this.lastRecipe.id()), (ServerPlayer) player);
    }
  }

  /**
   * Updates the recipe from the server
   * @param recipe  New recipe
   */
  public void updateRecipe(CraftingRecipe recipe) {
    this.lastRecipe = recipe == null ? null : new net.minecraft.world.item.crafting.RecipeHolder<>(net.minecraft.resources.ResourceLocation.parse("tconstruct:synced"), recipe);
    this.craftingResult.clearContent();
  }
}
