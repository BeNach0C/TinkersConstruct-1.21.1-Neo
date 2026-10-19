package slimeknights.tconstruct.plugin.jei.melting;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.recipe.melting.IDisplayableMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;

import java.awt.Color;
import java.util.List;

/** Shared logic between melting and foundry */
public abstract class AbstractMeltingCategory extends AbstractRecipeCategory<IDisplayableMeltingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  protected static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "melting.time");
  protected static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  protected static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  protected static final Component TOOLTIP_ORE = Component.translatable(TConstruct.makeTranslationKey("jei", "melting.ore"));
  /** Name of the fluid slot to fetch for the time tooltips. */
  protected static final String FLUID_SLOT = "fluid";

  /** Tooltip for fuel display */
  public static final FluidTooltipCallback FUEL_TOOLTIP = (fluid, slot, tooltip) -> {
    MeltingFuel fuel = MeltingFuelLookup.findFuel(fluid.getFluid());
    if (fuel != null) {
      tooltip.add(Component.translatable(KEY_TEMPERATURE, fuel.getTemperature()).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable(KEY_MULTIPLIER, fuel.getRate() / 10f).withStyle(ChatFormatting.GRAY));
    }
  };

  private final IDrawable background;
  protected final IDrawableStatic tankOverlay;
  protected final IDrawableStatic plus;

  public AbstractMeltingCategory(IGuiHelper helper, RecipeType<IDisplayableMeltingRecipe> recipeType, Component title, IDrawable icon) {
    super(recipeType, title, icon, 132, 40);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
    this.plus = helper.drawableBuilder(BACKGROUND_LOC, 132, 32, 8, 8)
                      .addPadding(2, 2, 2, 2)
                      .build();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayableMeltingRecipe recipe, IFocusGroup focuses) {
    // includes both the static arrow background and animated foreground
    int time = recipe.getTime();
    builder.addAnimatedRecipeArrow(time * 5, 56, 18);

    if (recipe.getOreType() != null) {
      builder.addDrawable(plus, 83, 26);
    }
    builder.addText(Component.translatable(KEY_TEMPERATURE, recipe.getTemperature()), 113, 9)
      .setPosition(0, 3)
      .setColor(Color.GRAY.getRGB())
      .setTextAlignment(HorizontalAlignment.CENTER);
  }

  @Override
  public void getTooltip(ITooltipBuilder tooltip, IDisplayableMeltingRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
    int time = recipe.getTime();
    // Arrow coordinates: (56, 18) to (56+24, 18+17)
    if (mouseX >= 56 && mouseX <= 80 && mouseY >= 18 && mouseY <= 35) {
      if (recipe.isTimeDynamic()) {
        slots.findSlotByName(FLUID_SLOT).ifPresent(fluidSlot -> {
          FluidStack fluid = fluidSlot.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
          tooltip.add(Component.translatable(KEY_COOLING_TIME, recipe.getTime(fluid) / 4));
        });
        return;
      }
      tooltip.add(Component.translatable(KEY_COOLING_TIME, time / 4));
      return;
    }
    // Plus icon coordinates: (83, 26) to (83+12, 26+12)
    if (recipe.getOreType() != null && mouseX >= 83 && mouseX <= 95 && mouseY >= 26 && mouseY <= 38) {
      tooltip.add(TOOLTIP_ORE);
    }
  }

  @Override
  public void draw(IDisplayableMeltingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    background.draw(graphics);
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  public static class MeltingFluidCallback implements FluidTooltipCallback {
    public static final MeltingFluidCallback INSTANCE = new MeltingFluidCallback();

    /**
     * Adds teh tooltip for ores
     *
     * @param stack  Fluid to draw
     * @param list   Tooltip so far
     * @return true if the amount is not in buckets
     */
    protected boolean appendMaterial(FluidStack stack, List<Component> list) {
      return FluidTooltipHandler.appendMaterialNoShift(stack.getFluid(), stack.getAmount(), list);
    }

    @Override
    public void onFluidTooltip(FluidStack fluid, IRecipeSlotView recipeSlotView, List<Component> tooltip) {
      if (appendMaterial(fluid, tooltip)) {
        FluidTooltipHandler.appendShift(tooltip);
      }
    }
  }

  @Override
  public ResourceLocation getRegistryName(IDisplayableMeltingRecipe recipe) {
    return recipe.getRecipeId();
  }
}
