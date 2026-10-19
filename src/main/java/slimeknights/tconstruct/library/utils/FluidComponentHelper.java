package slimeknights.tconstruct.library.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/** Helpers replacing the removed FluidStack NBT and PotionUtils APIs from 1.20 */
public final class FluidComponentHelper {
  private FluidComponentHelper() {}

  /** Gets a copy of the custom data on the fluid, replacement for {@code FluidStack#getTag()} */
  public static CompoundTag getTag(FluidStack fluid) {
    return fluid.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
  }

  /** Gets the potion contents on the fluid */
  public static PotionContents getPotion(FluidStack fluid) {
    return fluid.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
  }

  /** Gets all effects of the potion on the fluid, replacement for {@code PotionUtils.getPotion(tag).getEffects()} */
  public static List<MobEffectInstance> getEffects(FluidStack fluid) {
    List<MobEffectInstance> list = new ArrayList<>();
    getPotion(fluid).getAllEffects().forEach(list::add);
    return list;
  }
}

