package slimeknights.tconstruct.tools.recipe.severing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Builder for severing recipes that have only the base chance and looting bonus as fields */
@Setter
@Accessors(chain = true)
public class SpecialSeveringRecipeBuilder extends AbstractRecipeBuilder<SpecialSeveringRecipeBuilder> {
  private final RecipeSerializer<? extends SeveringRecipe> serializer;
  private float baseChance = 0.05f;
  private float lootingBonus = 0.01f;

  /** Creates a new builder for the given serializer. */
  public SpecialSeveringRecipeBuilder(RecipeSerializer<? extends SeveringRecipe> serializer) { this.serializer = serializer; }
  public static SpecialSeveringRecipeBuilder serializer(Supplier<? extends RecipeSerializer<? extends SeveringRecipe>> supplier) {
    return new SpecialSeveringRecipeBuilder(supplier.get());
  }

  /** Doubles the drop chances for this rare mob */
  public SpecialSeveringRecipeBuilder rareMob() {
    baseChance = 0.1f;
    lootingBonus = 0.02f;
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void save(RecipeOutput consumer) {
    save(consumer, Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer)));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    // We instantiate the recipe via reflection or hardcode since there are only 4.
    SeveringRecipe recipe;
    if (serializer == slimeknights.tconstruct.tools.TinkerModifiers.playerBeheadingSerializer.get()) {
        recipe = new PlayerBeheadingRecipe(id, baseChance, lootingBonus);
    } else if (serializer == slimeknights.tconstruct.tools.TinkerModifiers.snowGolemBeheadingSerializer.get()) {
        recipe = new SnowGolemBeheadingRecipe(id, baseChance, lootingBonus);
    } else if (serializer == slimeknights.tconstruct.tools.TinkerModifiers.mooshroomDemushroomingSerializer.get()) {
        recipe = new MooshroomDemushroomingRecipe(id, baseChance, lootingBonus);
    } else if (serializer == slimeknights.tconstruct.tools.TinkerModifiers.sheepShearing.get()) {
        recipe = new SheepShearingRecipe(id, baseChance, lootingBonus);
    } else {
        throw new IllegalStateException("Unknown special severing recipe");
    }
    consumer.accept(id, recipe, null);
  }

}
