package slimeknights.tconstruct.library.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.common.NBTLoadable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Helper to add NBT to vanilla recipes. Forge adds support but not the builders */
public record CraftingNBTWrapper(RecipeOutput recipe, CompoundTag nbt) /* TODO: was FinishedRecipe */ {
  @Override
  public void serializeRecipeData(JsonObject json) {
    recipe.serializeRecipeData(json);
    JsonObject result = GsonHelper.getAsJsonObject(json, "result");
    result.add("nbt", NBTLoadable.DISALLOW_STRING.serialize(nbt));
  }

  @Override
  public ResourceLocation getId() {
    return recipe.getId();
  }

  @Override
  public RecipeSerializer<?> getType() {
    return recipe.getType();
  }

  @Nullable
  @Override
  public JsonObject serializeAdvancement() {
    return recipe.serializeAdvancement();
  }

  @Nullable
  @Override
  public ResourceLocation getAdvancementId() {
    return recipe.getAdvancementId();
  }

  /** Creates a wrapped consumer, adding the given NBT */
  public static RecipeOutput wrap(RecipeOutput base, CompoundTag nbt) {
    return new RecipeOutput() {
        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            base.accept(id, new CraftingNBTWrapper(recipe, nbt), advancement, conditions);
        }
        @Override
        public net.minecraft.advancements.Advancement.Builder advancement() {
            return base.advancement();
        }
    };
  }
}
