package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialRecipe} */
@Deprecated
@NoArgsConstructor(staticName = "wrap")
public class ShapedMaterialConsumerBuilder {
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Adds a material to the builder */
  public ShapedMaterialConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public RecipeOutput build(RecipeOutput consumer) {
    return new RecipeOutput() {
        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder adv, ICondition... conditions) {
            if (recipe instanceof net.minecraft.world.item.crafting.ShapedRecipe shaped) {
                consumer.accept(id, new ShapedMaterialRecipe(shaped, materials), adv, conditions);
            }
        }
        @Override
        public net.minecraft.advancements.Advancement.Builder advancement() {
            return consumer.advancement();
        }
    };
  }
}
