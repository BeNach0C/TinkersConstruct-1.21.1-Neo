package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;

public interface TinkerTypeAwareRecipeSerializer<T extends Recipe<?>> extends TypeAwareRecipeSerializer<T> {
  ContextKey<TypeAwareRecipeSerializer<?>> TYPED_SERIALIZER = new ContextKey<>("typed_serializer");
}

