package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Shaped recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the material of the result.
 * @deprecated use {@link ShapedMaterialsRecipe}, which requires specifying the ingredients for each part.
 */
@Deprecated
public class ShapedMaterialRecipe extends ShapedRecipe {
  private MaterialValueIngredient material;
  private final List<MaterialVariantId> extraMaterials;
  public ShapedMaterialRecipe(String group, CraftingBookCategory category, net.minecraft.world.item.crafting.ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<MaterialVariantId> extraMaterials) {
    super(group, category, pattern, result, showNotification);
    this.extraMaterials = extraMaterials;
  }

  public ShapedMaterialRecipe(ShapedRecipe recipe, List<MaterialVariantId> extraMaterials) {
    this(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.getResultItem(null), recipe.showNotification(), extraMaterials);
  }

  /** Gets the material to match */
  @Nullable
  public MaterialValueIngredient getMaterial() {
    if (material == null) {
      // assume all material ingredients match the same stat type
      for (Ingredient ingredient : getIngredients()) {
        // collect all ingredients that match
        if (ingredient.getCustomIngredient() instanceof MaterialValueIngredient materialValue) {
          if (material == null) {
            material = materialValue;
          } else {
            // ensure the stat type matches, and expand the range
            material = material.merge(materialValue);
          }
        }
      }
      // if we found no materials, that is also an issue
      if (material == null) {
        TConstruct.LOG.error("No material ingredient found for material shaped recipe, this indicates a broken recipe");
      }
    }
    return material;
  }

  @Nullable
  private MaterialVariantId findMaterial(net.minecraft.world.item.crafting.CraftingInput inventory) {
    MaterialValueIngredient material = getMaterial();
    if (material == null) {
      return null;
    }
    // ensure same material in all slots
    MaterialVariantId firstMaterial = null;
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        // ignore anything that does not meet our requirements
        MaterialVariantId matchedMaterial = material.getMaterial(stack);
        if (matchedMaterial != null) {
          // first match is set
          if (firstMaterial == null) {
            firstMaterial = matchedMaterial;
          } else if (!firstMaterial.matchesVariant(matchedMaterial)) {
            // if same material but different variants, just discard the variant
            if (firstMaterial.getMaterialId().equals(matchedMaterial.getMaterialId())) {
              firstMaterial = firstMaterial.getMaterialId();
            } else {
              // if different materials, no match
              return null;
            }
          }
        }
      }
    }
    return firstMaterial;
  }

  @Override
  public boolean matches(net.minecraft.world.item.crafting.CraftingInput inventory, Level level) {
    if (!super.matches(inventory, level)) {
      return false;
    }

    // must have a material to match, no mixing
    return findMaterial(inventory) != null;
  }

  /** Sets the material for the given stack */
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    ShapedMaterialsRecipe.setMaterial(stack, material, extraMaterials);
  }

  @Override
  public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput inventory, net.minecraft.core.HolderLookup.Provider registryAccess) {
    ItemStack stack = super.assemble(inventory, registryAccess);
    MaterialVariantId material = findMaterial(inventory);
    if (material != null) {
      setMaterial(stack, material);
    }
    return stack;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapedMaterialRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedMaterialRecipe> {
    private static final slimeknights.mantle.data.loadable.Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = MaterialVariantId.LOADABLE.list(0);
    public static final com.mojang.serialization.MapCodec<ShapedMaterialRecipe> CODEC = new com.mojang.serialization.MapCodec<>() {
      @Override
      public <T> java.util.stream.Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
        return java.util.stream.Stream.empty();
      }

      @Override
      public <T> com.mojang.serialization.DataResult<ShapedMaterialRecipe> decode(com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.MapLike<T> input) {
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
          ShapedRecipe orig = net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.codec().codec()
            .parse(com.mojang.serialization.JsonOps.INSTANCE, json)
            .getOrThrow(JsonSyntaxException::new);
          ItemStack result = orig.getResultItem(null);
          if (nbtElement != null) {
            net.minecraft.nbt.CompoundTag tag = slimeknights.mantle.data.loadable.common.NBTLoadable.ALLOW_STRING.convert(nbtElement, "nbt");
            result = result.copy();
            result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
          }
          List<MaterialVariantId> extraMaterials = EXTRA_MATERIALS.getOrDefault(json, "extra_materials", List.of());
          return com.mojang.serialization.DataResult.success(
            new ShapedMaterialRecipe(orig.getGroup(), orig.category(), orig.pattern, result, orig.showNotification(), extraMaterials));
        } catch (Exception e) {
          return com.mojang.serialization.DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage());
        }
      }

      @Override
      public <T> com.mojang.serialization.RecordBuilder<T> encode(ShapedMaterialRecipe input, com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> prefix) {
        return net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.codec().encode(input, ops, prefix);
      }
    };

    @Override
    public com.mojang.serialization.MapCodec<ShapedMaterialRecipe> codec() {
      return CODEC;
    }

    @Override
    public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapedMaterialRecipe> streamCodecSafe() {
      return new net.minecraft.network.codec.StreamCodec<>() {
        @Override
        public ShapedMaterialRecipe decode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
          ShapedRecipe recipe = net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
          int extraSize = buffer.readVarInt();
          List<MaterialVariantId> extraMaterials = new java.util.ArrayList<>(extraSize);
          for (int i = 0; i < extraSize; i++) {
            extraMaterials.add(MaterialVariantId.fromNetwork(buffer));
          }
          return new ShapedMaterialRecipe(recipe, extraMaterials);
        }

        @Override
        public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer, ShapedMaterialRecipe recipe) {
          net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe);
          buffer.writeVarInt(recipe.extraMaterials.size());
          for (MaterialVariantId extra : recipe.extraMaterials) {
            extra.toNetwork(buffer);
          }
        }
      };
    }
  }
}
