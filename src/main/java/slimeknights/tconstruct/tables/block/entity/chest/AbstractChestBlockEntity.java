package slimeknights.tconstruct.tables.block.entity.chest;
import net.neoforged.neoforge.capabilities.Capabilities;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import slimeknights.mantle.block.entity.NameableBlockEntity;
import slimeknights.tconstruct.tables.block.entity.inventory.IChestItemHandler;
import slimeknights.tconstruct.tables.menu.TinkerChestContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Shared base logic for all Tinkers' chest tile entities */
public abstract class AbstractChestBlockEntity extends NameableBlockEntity {
  private static final String KEY_ITEMS = "Items";

  @Getter
  private final IChestItemHandler itemHandler;
  protected AbstractChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, IChestItemHandler itemHandler) {
    super(type, pos, state, name);
    itemHandler.setParent(this);
    this.itemHandler = itemHandler;
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int menuId, Inventory playerInventory, Player playerEntity) {
    return new TinkerChestContainerMenu(menuId, playerInventory, this);
  }

  /**
   * Checks if the given item should be inserted into the chest on interact
   * @param player    Player inserting
   * @param heldItem  Stack to insert
   * @return  Return true
   */
  public boolean canInsert(Player player, ItemStack heldItem) {
    return true;
  }

    public IChestItemHandler getItemHandler() {
    return itemHandler;
  }

  @Override
  public void saveAdditional(CompoundTag tags, net.minecraft.core.HolderLookup.Provider provider) {
    super.saveAdditional(tags, provider);
    CompoundTag handlerNBT = itemHandler.serializeNBT(provider);
    tags.put(KEY_ITEMS, handlerNBT.getList(KEY_ITEMS, Tag.TAG_COMPOUND));
  }

  public void readInventory(CompoundTag tags, net.minecraft.core.HolderLookup.Provider provider) {
    CompoundTag handlerNBT = new CompoundTag();
    handlerNBT.put(KEY_ITEMS, tags.getList(KEY_ITEMS, Tag.TAG_COMPOUND));
    itemHandler.deserializeNBT(provider, handlerNBT);
  }

  public void readInventory(CompoundTag tags) {
    net.minecraft.core.HolderLookup.Provider provider = level != null ? level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
    readInventory(tags, provider);
  }

  @Override
  public void loadAdditional(CompoundTag tags, net.minecraft.core.HolderLookup.Provider provider) {
    super.loadAdditional(tags, provider);
    readInventory(tags, provider);
  }
}
