package slimeknights.tconstruct.library.recipe.fuel;

import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.container.IEmptyContainer;

/**
 * Inventory containing just a single fluid
 */
public interface IFluidContainer extends IEmptyContainer, net.minecraft.world.item.crafting.RecipeInput {
  /**
   * Gets the fluid contained in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();
  @Override default net.minecraft.world.item.ItemStack getItem(int index) { return net.minecraft.world.item.ItemStack.EMPTY; }
  @Override default int size() { return 0; }
  @Override default boolean isEmpty() { return getFluid() == null || getFluid() == net.minecraft.world.level.material.Fluids.EMPTY; }
}
