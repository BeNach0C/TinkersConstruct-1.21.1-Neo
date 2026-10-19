package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/** Ingredient that only matches tools with a specific hook */
public class ToolHookIngredient implements ICustomIngredient {
  private final TagKey<Item> tag;
  private final ModuleHook<?> hook;

  protected ToolHookIngredient(TagKey<Item> tag, ModuleHook<?> hook) {
    this.tag = tag;
    this.hook = hook;
  }

  public static ToolHookIngredient of(TagKey<Item> tag, ModuleHook<?> hook) {
    return new ToolHookIngredient(tag, hook);
  }

  public static ToolHookIngredient of(ModuleHook<?> hook) {
    return of(TinkerTags.Items.MODIFIABLE, hook);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && stack.is(tag) && stack.getItem() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook);
  }

  @Override
  public Stream<ItemStack> getItems() {
    List<ItemStack> list = new ArrayList<>();
    for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
      if (holder.value() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook)) {
        list.add(new ItemStack(modifiable));
      }
    }
    return list.stream();
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  public static final ResourceLocation ID = TConstruct.getResource("tool_hook");
  public static final net.neoforged.neoforge.common.crafting.IngredientType<ToolHookIngredient> TYPE = slimeknights.mantle.recipe.helper.LoadableIngredientSerializer.of(
    json -> new ToolHookIngredient(
      Loadables.ITEM_TAG.getOrDefault(json, "tag", TinkerTags.Items.MODIFIABLE),
      ToolHooks.LOADER.getIfPresent(json, "hook")
    ),
    (ing, json) -> {
      json.add("tag", Loadables.ITEM_TAG.serialize(ing.tag));
      json.add("hook", ToolHooks.LOADER.serialize(ing.hook));
    },
    buffer -> new ToolHookIngredient(
      Loadables.ITEM_TAG.decode(buffer),
      ToolHooks.LOADER.decode(buffer)
    ),
    (buffer, ing) -> {
      Loadables.ITEM_TAG.encode(buffer, ing.tag);
      ToolHooks.LOADER.encode(buffer, ing.hook);
    }
  );

  @Override
  public net.neoforged.neoforge.common.crafting.IngredientType<?> getType() {
    return TYPE;
  }
}
