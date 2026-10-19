package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.container.ISingleStackContainer;

import javax.annotation.Nullable;

/**
 * Inventory containing a single item and a fluid
 */
public interface ICastingContainer extends ISingleStackContainer, net.minecraft.world.item.crafting.RecipeInput {
  @Override
  default int size() {
    return 1;
  }

  @Override
  default net.minecraft.world.item.ItemStack getItem(int index) {
    return index == 0 ? getStack() : net.minecraft.world.item.ItemStack.EMPTY;
  }

  @Override
  default boolean isEmpty() {
    return getStack().isEmpty() && (getFluid() == null || getFluid() == net.minecraft.world.level.material.Fluids.EMPTY);
  }

  /**
   * Gets the contained fluid in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();

  /**
   * Gets the NBT for the contained fluid
   * @return  Fluid's NBT
   */
  @Nullable
  default CompoundTag getFluidTag() {
    return null;
  }
}
