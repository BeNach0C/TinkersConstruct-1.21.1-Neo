package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;

/** Ingredient matching an item with no container item, used to ensure NBT fluid items are empty */
public class NoContainerIngredient extends NestedIngredient {
  public static final ResourceLocation ID = TConstruct.getResource("no_container");

  protected NoContainerIngredient(Ingredient nested) {
    super(nested);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && super.test(stack) && !stack.hasCraftingRemainingItem();
  }

  @Override
  public boolean isSimple() {
    return false;
  }



  public static final net.neoforged.neoforge.common.crafting.IngredientType<NoContainerIngredient> TYPE = slimeknights.mantle.recipe.helper.LoadableIngredientSerializer.of(
    json -> {
      Ingredient ingredient;
      if (json.has("match")) {
        ingredient = slimeknights.mantle.data.loadable.common.IngredientLoadable.DISALLOW_EMPTY.convert(json.get("match"), "match");
      } else {
        JsonObject copy = json.deepCopy();
        copy.remove("type");
        ingredient = slimeknights.mantle.data.loadable.common.IngredientLoadable.DISALLOW_EMPTY.convert(copy, "match");
      }
      return new NoContainerIngredient(ingredient);
    },
    (ing, json) -> json.add("match", slimeknights.mantle.data.loadable.common.IngredientLoadable.DISALLOW_EMPTY.serialize(ing.nested)),
    buffer -> new NoContainerIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)),
    (buffer, ing) -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing.nested)
  );

  @Override
  public net.neoforged.neoforge.common.crafting.IngredientType<?> getType() {
    return TYPE;
  }


  /* Static constructors */

  /** Creates an instance from the given nested ingredient */
  public static NoContainerIngredient of(Ingredient ingredient) {
    return new NoContainerIngredient(ingredient);
  }

  /** Creates an instance from the given items */
  public static NoContainerIngredient of(ItemLike... items) {
    return of(Ingredient.of(items));
  }

  /** Creates an instance from the given stacks */
  public static NoContainerIngredient of(ItemStack... stacks) {
    return of(Ingredient.of(stacks));
  }

  /** Creates an instance from the given tag */
  public static NoContainerIngredient of(TagKey<Item> tag) {
    return of(Ingredient.of(tag));
  }
}
