package slimeknights.tconstruct.common.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

import javax.annotation.Nullable;

/**
 * Utility for saving recipes that use a RecordLoadable serializer.
 * In 1.21+, FinishedRecipe no longer exists. Recipes are saved directly via RecipeOutput.accept().
 *
 * Previously this was a record implementing FinishedRecipe; now it's a wrapper that
 * extracts the necessary info and passes it to RecipeOutput.
 */
public record LoadableFinishedRecipe<T extends Recipe<?>>(RecordLoadable<T> loader, T recipe) {

  /**
   * Saves this recipe to the given RecipeOutput.
   * Replaces the old consumer.accept(finishedRecipe) pattern.
   */
  public void save(RecipeOutput consumer, ResourceLocation id) {
    consumer.accept(id, recipe, null);
  }

  /** One-liner to accept a new recipe using the old API pattern */
  public static <T extends Recipe<?>> void save(RecipeOutput consumer, RecordLoadable<T> loader, T recipe) {
    // TODO: Need to get the recipe ID from somewhere - in 1.21 recipes don't have getId() directly
    // For now this will need the caller to provide an ID
    throw new UnsupportedOperationException("LoadableFinishedRecipe.save needs to be updated for 1.21 - callers should use consumer.accept(id, recipe, null) directly");
  }
}
