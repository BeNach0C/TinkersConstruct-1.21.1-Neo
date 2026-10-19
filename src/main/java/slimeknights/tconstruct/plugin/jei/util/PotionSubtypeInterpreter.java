package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.tconstruct.library.utils.FluidComponentHelper;

/** Common logic for subtype interpreter between the fluid and item form of our potion. */
public final class PotionSubtypeInterpreter {
  private PotionSubtypeInterpreter() {}

  public static final ISubtypeInterpreter<ItemStack> ITEM = new ISubtypeInterpreter<>() {
    @Override
    public Object getSubtypeData(ItemStack stack, UidContext context) {
      PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
      if (contents == null) {
        return "";
      }
      return contents.potion().map(p -> p.unwrapKey().map(k -> k.location().toString()).orElse("")).orElse("");
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
      return "";
    }
  };

  public static final ISubtypeInterpreter<FluidStack> FLUID = new ISubtypeInterpreter<>() {
    @Override
    public Object getSubtypeData(FluidStack stack, UidContext context) {
      PotionContents contents = FluidComponentHelper.getPotion(stack);
      return contents.potion().map(p -> p.unwrapKey().map(k -> k.location().toString()).orElse("")).orElse("");
    }

    @Override
    public String getLegacyStringSubtypeInfo(FluidStack ingredient, UidContext context) {
      return "";
    }
  };
}
