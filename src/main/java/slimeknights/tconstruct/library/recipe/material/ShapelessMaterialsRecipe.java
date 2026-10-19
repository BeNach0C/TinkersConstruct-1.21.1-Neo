package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Shapeless recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient} and
 * {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the materials of the result.
 */
public class ShapelessMaterialsRecipe extends ShapelessRecipe implements MaterialsCraftingTableRecipe {
  /** Number of parts to match */
  @Getter
  private final int partCount;
  /** List of additional materials to add beyond the parts */
  @Getter
  private final List<MaterialVariantId> extraMaterials;

  public ShapelessMaterialsRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients, int partCount, List<MaterialVariantId> extraMaterials) {
    super(group, category, result, ingredients);
    this.partCount = partCount;
    this.extraMaterials = extraMaterials;
  }

  public ShapelessMaterialsRecipe(ShapelessRecipe recipe, int partCount, List<MaterialVariantId> extraMaterials) {
    this(recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients(), partCount, extraMaterials);
  }

  @Override
  public List<Ingredient> getParts() {
    return getIngredients();
  }

  /** Sets the material for the given stack */
  @Override
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    ShapedMaterialsRecipe.setMaterial(stack, material, extraMaterials);
  }

  @Override
  public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput inventory, net.minecraft.core.HolderLookup.Provider registryAccess) {
    return ShapedMaterialsRecipe.assemble(super.assemble(inventory, registryAccess), inventory, getIngredients(), partCount, false, extraMaterials);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapelessMaterialsRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapelessMaterialsRecipe> {
    private static final slimeknights.mantle.data.loadable.Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = MaterialVariantId.LOADABLE.list(0);
    public static final com.mojang.serialization.MapCodec<ShapelessMaterialsRecipe> CODEC = new com.mojang.serialization.MapCodec<>() {
      @Override
      public <T> java.util.stream.Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
        return java.util.stream.Stream.empty();
      }

      @Override
      public <T> com.mojang.serialization.DataResult<ShapelessMaterialsRecipe> decode(com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.MapLike<T> input) {
        try {
          JsonObject json = ops.convertTo(com.mojang.serialization.JsonOps.INSTANCE, ops.createMap(input.entries())).getAsJsonObject();
          com.google.gson.JsonElement nbtElement = null;
          if (json.has("result") && json.get("result").isJsonObject()) {
            JsonObject resultObj = json.getAsJsonObject("result");
            if (resultObj.has("item") && !resultObj.has("id")) {
              resultObj.add("id", resultObj.get("item"));
              resultObj.remove("item");
            }
            if (resultObj.has("nbt")) {
              nbtElement = resultObj.remove("nbt");
            }
          }
          ShapelessRecipe orig = net.minecraft.world.item.crafting.RecipeSerializer.SHAPELESS_RECIPE.codec().codec()
            .parse(com.mojang.serialization.JsonOps.INSTANCE, json)
            .getOrThrow(JsonSyntaxException::new);
          ItemStack result = orig.getResultItem(null);
          if (nbtElement != null) {
            net.minecraft.nbt.CompoundTag tag = slimeknights.mantle.data.loadable.common.NBTLoadable.ALLOW_STRING.convert(nbtElement, "nbt");
            result = result.copy();
            result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
          }
          int partCount = GsonHelper.getAsInt(json, "parts", 1);
          List<MaterialVariantId> extraMaterials = EXTRA_MATERIALS.getOrDefault(json, "extra_materials", List.of());
          return com.mojang.serialization.DataResult.success(
            new ShapelessMaterialsRecipe(orig.getGroup(), orig.category(), result, orig.getIngredients(), partCount, extraMaterials));
        } catch (Exception e) {
          return com.mojang.serialization.DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage());
        }
      }

      @Override
      public <T> com.mojang.serialization.RecordBuilder<T> encode(ShapelessMaterialsRecipe input, com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> prefix) {
        return net.minecraft.world.item.crafting.RecipeSerializer.SHAPELESS_RECIPE.codec().encode(input, ops, prefix);
      }
    };

    @Override
    public com.mojang.serialization.MapCodec<ShapelessMaterialsRecipe> codec() {
      return CODEC;
    }

    @Override
    public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapelessMaterialsRecipe> streamCodecSafe() {
      return new net.minecraft.network.codec.StreamCodec<>() {
        @Override
        public ShapelessMaterialsRecipe decode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
          ShapelessRecipe recipe = net.minecraft.world.item.crafting.RecipeSerializer.SHAPELESS_RECIPE.streamCodec().decode(buffer);
          int partCount = buffer.readVarInt();
          int extraSize = buffer.readVarInt();
          List<MaterialVariantId> extraMaterials = new ArrayList<>(extraSize);
          for (int i = 0; i < extraSize; i++) {
            extraMaterials.add(MaterialVariantId.fromNetwork(buffer));
          }
          return new ShapelessMaterialsRecipe(recipe, partCount, extraMaterials);
        }

        @Override
        public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer, ShapelessMaterialsRecipe recipe) {
          net.minecraft.world.item.crafting.RecipeSerializer.SHAPELESS_RECIPE.streamCodec().encode(buffer, recipe);
          buffer.writeVarInt(recipe.partCount);
          buffer.writeVarInt(recipe.extraMaterials.size());
          for (MaterialVariantId extra : recipe.extraMaterials) {
            extra.toNetwork(buffer);
          }
        }
      };
    }
  }
}
