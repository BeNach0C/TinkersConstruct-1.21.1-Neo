package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialsRecipe} and {@link ShapelessMaterialsRecipe} */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialsConsumerBuilder {
  private final String parts;
  private final int partCount;
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Creates a new shaped recipe with the given ingredients as parts */
  public static MaterialsConsumerBuilder shaped(String parts) {
    if (parts.isEmpty()) {
      throw new IllegalArgumentException("Parts may not be empty");
    }
    return new MaterialsConsumerBuilder(parts, 0);
  }

  /** Creates a new shapeless recipe with the first ingredients as parts */
  public static MaterialsConsumerBuilder shapeless(int parts) {
    if (parts <= 0) {
      throw new IllegalArgumentException("Parts must be greater than 0");
    }
    return new MaterialsConsumerBuilder("", parts);
  }

  /** Adds a material to the builder */
  public MaterialsConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public RecipeOutput build(RecipeOutput consumer) {
    return new RecipeOutput() {
        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder adv, ICondition... conditions) {
            if (recipe instanceof net.minecraft.world.item.crafting.ShapedRecipe shaped) {
                consumer.accept(id, new ShapedMaterialsRecipe(shaped, java.util.List.of(), materials), adv, conditions);
            } else if (recipe instanceof net.minecraft.world.item.crafting.ShapelessRecipe shapeless) {
                consumer.accept(id, new ShapelessMaterialsRecipe(shapeless, partCount > 0 ? partCount : 1, materials), adv, conditions);
            } else {
                consumer.accept(id, recipe, adv, conditions);
            }
        }
        @Override
        public net.minecraft.advancements.Advancement.Builder advancement() {
            return consumer.advancement();
        }
    };
  }
}
