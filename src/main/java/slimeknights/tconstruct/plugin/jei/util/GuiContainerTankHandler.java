package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank.FluidLocation;

import java.util.Optional;

/**
 * Class to pass {@link IScreenWithFluidTank} into JEI
 */
public class GuiContainerTankHandler<C extends AbstractContainerMenu, T extends AbstractContainerScreen<C> & IScreenWithFluidTank> implements IGuiContainerHandler<T> {
  @Override
  public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
    FluidLocation fluid = containerScreen.getFluidUnderMouse((int)mouseX, (int)mouseY);
    if (fluid != null) {
      ITypedIngredient<FluidStack> typed = new ITypedIngredient<>() {
        @Override
        public IIngredientType<FluidStack> getType() {
          return NeoForgeTypes.FLUID_STACK;
        }

        @Override
        public FluidStack getIngredient() {
          return fluid.fluid();
        }
      };

      IClickableIngredient<FluidStack> clickable = new IClickableIngredient<>() {
        @Override
        public ITypedIngredient<FluidStack> getTypedIngredient() {
          return typed;
        }

        @Override
        public Rect2i getArea() {
          return fluid.location();
        }
      };

      return Optional.of(clickable);
    }
    return Optional.empty();
  }
}

