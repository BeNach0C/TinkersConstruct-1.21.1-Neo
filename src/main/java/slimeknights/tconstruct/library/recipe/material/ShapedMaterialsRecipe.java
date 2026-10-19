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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shaped recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient} and
 * {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the materials of the result.
 */
public class ShapedMaterialsRecipe extends ShapedRecipe implements MaterialsCraftingTableRecipe {
  /** List of tool parts to search for in the final recipe */
  @Getter
  private final List<Ingredient> parts;
  /**
   * If true, a part may show up multiple times in the inputs, and all copies should match.
   * If false, only the first instance of a part is checked for each input, allowing a tool with the same part multiple times.
   */
  private final boolean checkRepeats;
  /** List of additional materials to add beyond the parts */
  @Getter
  private final List<MaterialVariantId> extraMaterials;
  public ShapedMaterialsRecipe(String group, CraftingBookCategory category, net.minecraft.world.item.crafting.ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<Ingredient> parts, List<MaterialVariantId> extraMaterials) {
    super(group, category, pattern, result, showNotification);
    this.parts = parts;
    this.checkRepeats = parts.stream().unordered().distinct().count() == parts.size();
    this.extraMaterials = extraMaterials;
  }

  public ShapedMaterialsRecipe(ShapedRecipe recipe, List<Ingredient> parts, List<MaterialVariantId> extraMaterials) {
    this(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.getResultItem(null), recipe.showNotification(), parts, extraMaterials);
  }

  @Override
  public int getPartCount() {
    return parts.size();
  }

  /**
   * Finds materials for each of the parts
   * @return Array of all matched materials. Array will have no null entries, though the array may be null if no match was found.
   */
  @Nullable
  static MaterialVariantId[] findMaterials(net.minecraft.world.item.crafting.CraftingInput inventory, List<Ingredient> parts, int partCount, boolean checkRepeats) {
    // want one material for each
    MaterialVariantId[] materials = new MaterialVariantId[partCount];
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        for (int p = 0; p < partCount; p++) {
          MaterialVariantId current = materials[p];
          // if we have not found the material yet, or repeats are considered the same material, test the ingredient
          if ((current == null || checkRepeats) && parts.get(p).test(stack)) {
            MaterialVariantId matched = MaterialRecipeCache.getMaterial(stack);
            // first occurrence? thats our material
            if (current == null) {
              materials[p] = matched;
              break;
            } else if (!current.matchesVariant(matched)) {
              // if same material but different variants, just discard the variant
              if (current.getMaterialId().equals(matched.getMaterialId())) {
                materials[p] = current.getMaterialId();
                break;
              } else {
                // if different materials, no match
                return null;
              }
            }
          }
        }
      }
    }
    // ensure we found all materials needed
    for (int p = 0; p < partCount; p++) {
      if (materials[p] == null) {
        return null;
      }
    }
    return materials;
  }

  @Override
  public boolean matches(net.minecraft.world.item.crafting.CraftingInput inventory, Level level) {
    if (!super.matches(inventory, level)) {
      return false;
    }
    // ensure all part materials matched and we found all parts
    return findMaterials(inventory, parts, parts.size(), checkRepeats) != null;
  }

  /** Common logic to this and {@link ShapedMaterialsRecipe} */
  public static void setMaterial(ItemStack stack, MaterialVariantId material, List<MaterialVariantId> extraMaterials) {
    if (extraMaterials.isEmpty() && stack.getItem() instanceof IMaterialItem materialItem) {
      materialItem.setMaterial(stack, material);
    } else {
      MaterialNBT.Builder builder = MaterialNBT.builder();
      builder.add(material);
      for (MaterialVariantId extraMaterial : extraMaterials) {
        builder.add(extraMaterial);
      }
      ToolStack.from(stack).setMaterials(builder.build());
    }
  }

  /** Sets the material for the given stack */
  @Override
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    setMaterial(stack, material, extraMaterials);
  }

  /** Assembles the item with material information */
  static ItemStack assemble(ItemStack stack, net.minecraft.world.item.crafting.CraftingInput inventory, List<Ingredient> parts, int partCount, boolean checkRepeats, List<MaterialVariantId> extraMaterials) {
    MaterialVariantId[] materials = findMaterials(inventory, parts, partCount, checkRepeats);
    if (materials != null) {
      // if the result is a tool part, and we only have the one material, set its material
      if (materials.length == 1 && extraMaterials.isEmpty() && stack.getItem() instanceof IMaterialItem materialItem) {
        return materialItem.setMaterial(stack, materials[0]);
      }
      MaterialNBT.Builder builder = MaterialNBT.builder();
      // add each material
      for (MaterialVariantId material : materials) {
        builder.add(material);
      }
      // add extra materials
      builder.add(extraMaterials);
      ToolStack.from(stack).setMaterials(builder.build());
    }
    return stack;
  }

  @Override
  public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput inventory, net.minecraft.core.HolderLookup.Provider registryAccess) {
    return assemble(super.assemble(inventory, registryAccess), inventory, parts, parts.size(), checkRepeats, extraMaterials);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapedMaterialsRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedMaterialsRecipe> {
    private static final slimeknights.mantle.data.loadable.Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = MaterialVariantId.LOADABLE.list(0);
    public static final com.mojang.serialization.MapCodec<ShapedMaterialsRecipe> CODEC = new com.mojang.serialization.MapCodec<>() {
      @Override
      public <T> java.util.stream.Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
        return java.util.stream.Stream.empty();
      }

      @Override
      public <T> com.mojang.serialization.DataResult<ShapedMaterialsRecipe> decode(com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.MapLike<T> input) {
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
          List<Ingredient> parts = new ArrayList<>();
          com.google.gson.JsonElement partsEl = json.get("parts");
          if (partsEl != null && partsEl.isJsonPrimitive()) {
            String partStr = partsEl.getAsString();
            JsonObject keyObj = GsonHelper.getAsJsonObject(json, "key");
            for (int i = 0; i < partStr.length(); i++) {
              String k = String.valueOf(partStr.charAt(i));
              if (!keyObj.has(k)) {
                throw new JsonSyntaxException("Missing key '" + k + "' referenced in parts '" + partStr + "'");
              }
              parts.add(slimeknights.mantle.data.loadable.common.IngredientLoadable.DISALLOW_EMPTY.convert(keyObj.get(k), "key." + k));
            }
          } else if (partsEl != null && partsEl.isJsonArray()) {
            for (com.google.gson.JsonElement el : partsEl.getAsJsonArray()) {
              parts.add(slimeknights.mantle.data.loadable.common.IngredientLoadable.DISALLOW_EMPTY.convert(el, "parts"));
            }
          }
          List<MaterialVariantId> extraMaterials = EXTRA_MATERIALS.getOrDefault(json, "extra_materials", List.of());
          return com.mojang.serialization.DataResult.success(
            new ShapedMaterialsRecipe(orig.getGroup(), orig.category(), orig.pattern, result, orig.showNotification(), parts, extraMaterials));
        } catch (Exception e) {
          return com.mojang.serialization.DataResult.error(() -> e.getMessage() == null ? e.toString() : e.getMessage());
        }
      }

      @Override
      public <T> com.mojang.serialization.RecordBuilder<T> encode(ShapedMaterialsRecipe input, com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> prefix) {
        return net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.codec().encode(input, ops, prefix);
      }
    };

    @Override
    public com.mojang.serialization.MapCodec<ShapedMaterialsRecipe> codec() {
      return CODEC;
    }

    @Override
    public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ShapedMaterialsRecipe> streamCodecSafe() {
      return new net.minecraft.network.codec.StreamCodec<>() {
        @Override
        public ShapedMaterialsRecipe decode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
          ShapedRecipe recipe = net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
          int partsSize = buffer.readVarInt();
          List<Ingredient> parts = new ArrayList<>(partsSize);
          for (int i = 0; i < partsSize; i++) {
            parts.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
          }
          int extraSize = buffer.readVarInt();
          List<MaterialVariantId> extraMaterials = new ArrayList<>(extraSize);
          for (int i = 0; i < extraSize; i++) {
            extraMaterials.add(MaterialVariantId.fromNetwork(buffer));
          }
          return new ShapedMaterialsRecipe(recipe, parts, extraMaterials);
        }

        @Override
        public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer, ShapedMaterialsRecipe recipe) {
          net.minecraft.world.item.crafting.RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe);
          buffer.writeVarInt(recipe.parts.size());
          for (Ingredient part : recipe.parts) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, part);
          }
          buffer.writeVarInt(recipe.extraMaterials.size());
          for (MaterialVariantId extra : recipe.extraMaterials) {
            extra.toNetwork(buffer);
          }
        }
      };
    }
  }
}
