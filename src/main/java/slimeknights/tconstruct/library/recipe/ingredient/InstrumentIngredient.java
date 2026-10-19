package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/** Ingredient matching an {@link net.minecraft.world.item.InstrumentItem} with a particular instrument. */
public class InstrumentIngredient implements ICustomIngredient {
  private static final String TAG_INSTRUMENT = "instrument";
  public static final ResourceLocation ID = TConstruct.getResource("instrument");

  private final Item item;
  @Nullable
  private final ResourceKey<Instrument> instrument;
  @Nullable
  private final TagKey<Instrument> ignore;
  protected InstrumentIngredient(Item item, @Nullable ResourceKey<Instrument> instrument, @Nullable TagKey<Instrument> ignore) {
    this.item = item.asItem();
    this.instrument = instrument;
    this.ignore = ignore;
  }

  /** Creates a new instance matching the given instrument */
  public static InstrumentIngredient of(ItemLike item, ResourceKey<Instrument> instrument) {
    return new InstrumentIngredient(item.asItem(), instrument, null);
  }

  /** Creates a new instance ignoring the given tag */
  public static InstrumentIngredient of(ItemLike item, TagKey<Instrument> ignore) {
    return new InstrumentIngredient(item.asItem(), null, ignore);
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || !stack.is(item)) {
      return false;
    }
    Holder<Instrument> inst = stack.get(net.minecraft.core.component.DataComponents.INSTRUMENT);
    if (inst != null) {
      if (this.instrument != null) {
        return inst.is(this.instrument);
      }
      if (this.ignore != null) {
        return !inst.is(this.ignore);
      }
      return true;
    }
    return this.instrument == null;
  }

  @Override
  public Stream<ItemStack> getItems() {
    ItemStack stack = new ItemStack(item);
    if (instrument != null) {
      BuiltInRegistries.INSTRUMENT.getHolder(instrument).ifPresent(h -> stack.set(net.minecraft.core.component.DataComponents.INSTRUMENT, h));
    }
    return Stream.of(stack);
  }

  public static final net.neoforged.neoforge.common.crafting.IngredientType<InstrumentIngredient> TYPE = slimeknights.mantle.recipe.helper.LoadableIngredientSerializer.of(
    json -> {
      Item item = Loadables.ITEM.getIfPresent(json, "item");
      ResourceKey<Instrument> instrument = null;
      TagKey<Instrument> ignore = null;
      if (json.has("instrument")) {
        instrument = ResourceKey.create(Registries.INSTRUMENT, JsonHelper.getResourceLocation(json, "instrument"));
      } else if (json.has("ignore")) {
        ignore = TinkerLoadables.INSTRUMENT_TAGS.getIfPresent(json, "ignore");
      } else {
        throw new JsonSyntaxException("Invalid InstrumentIngredient: must set either 'instrument' or 'ignore'");
      }
      return new InstrumentIngredient(item, instrument, ignore);
    },
    (ing, json) -> {
      json.add("item", Loadables.ITEM.serialize(ing.item));
      if (ing.instrument != null) {
        json.addProperty("instrument", ing.instrument.location().toString());
      } else if (ing.ignore != null) {
        json.add("ignore", TinkerLoadables.INSTRUMENT_TAGS.serialize(ing.ignore));
      }
    },
    buffer -> {
      Item item = Loadables.ITEM.decode(buffer);
      ResourceKey<Instrument> instrument = null;
      TagKey<Instrument> ignore = null;
      if (buffer.readBoolean()) {
        instrument = ResourceKey.create(Registries.INSTRUMENT, buffer.readResourceLocation());
      } else {
        ignore = TagKey.create(Registries.INSTRUMENT, buffer.readResourceLocation());
      }
      return new InstrumentIngredient(item, instrument, ignore);
    },
    (buffer, ing) -> {
      Loadables.ITEM.encode(buffer, ing.item);
      if (ing.instrument != null) {
        buffer.writeBoolean(true);
        buffer.writeResourceLocation(ing.instrument.location());
      } else {
        assert ing.ignore != null;
        buffer.writeBoolean(false);
        buffer.writeResourceLocation(ing.ignore.location());
      }
    }
  );

  @Override
  public net.neoforged.neoforge.common.crafting.IngredientType<?> getType() {
    return TYPE;
  }
}
