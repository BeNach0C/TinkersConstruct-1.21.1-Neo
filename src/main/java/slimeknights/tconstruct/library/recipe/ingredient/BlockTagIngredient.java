package slimeknights.tconstruct.library.recipe.ingredient;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Item ingredient matching items with a block form in the given tag */
@RequiredArgsConstructor
public class BlockTagIngredient implements ICustomIngredient {
  public static final ResourceLocation ID = TConstruct.getResource("block_tag");
  private final TagKey<Block> tag;
  @Nullable
  private Set<Item> matchingItems;
  @Nullable
  private ItemStack[] items;

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && getMatchingItems().contains(stack.getItem());
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  public void invalidate() {
    this.matchingItems = null;
    this.items = null;
  }

  /** Gets the ordered matching items set */
  @SuppressWarnings("deprecation")
  private Set<Item> getMatchingItems() {
    if (matchingItems == null) {
      matchingItems = RegistryHelper.getTagValueStream(BuiltInRegistries.BLOCK, tag)
                                    .map(Block::asItem)
                                    .filter(item -> item != Items.AIR)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    return matchingItems;
  }

  @Override
  public Stream<ItemStack> getItems() {
    if (items == null) {
      items = getMatchingItems().stream().map(ItemStack::new).toArray(ItemStack[]::new);
    }
    return Arrays.stream(items);
  }

  public static final net.neoforged.neoforge.common.crafting.IngredientType<BlockTagIngredient> TYPE = slimeknights.mantle.recipe.helper.LoadableIngredientSerializer.of(
    json -> new BlockTagIngredient(Loadables.BLOCK_TAG.getIfPresent(json, "tag")),
    (ing, json) -> json.add("tag", Loadables.BLOCK_TAG.serialize(ing.tag)),
    buffer -> new BlockTagIngredient(Loadables.BLOCK_TAG.decode(buffer)),
    (buffer, ing) -> Loadables.BLOCK_TAG.encode(buffer, ing.tag)
  );

  @Override
  public net.neoforged.neoforge.common.crafting.IngredientType<?> getType() {
    return TYPE;
  }
}
