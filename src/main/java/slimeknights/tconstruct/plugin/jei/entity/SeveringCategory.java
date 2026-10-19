package slimeknights.tconstruct.plugin.jei.entity;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public class SeveringCategory extends AbstractRecipeCategory<SeveringRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "severing.title");

  public SeveringCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.SEVERING, TITLE, helper.createDrawableItemStack(TinkerTools.cleaver.get().getRenderTool()), 100, 38);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, SeveringRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrow(42, 10);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, SeveringRecipe recipe, IFocusGroup focuses) {
    EntityIngredient input = recipe.getIngredient();
    List<ItemStack> eggStacks = input.getEggs().stream().filter(stack -> !stack.isEmpty()).toList();
    if (!eggStacks.isEmpty()) {
      builder.addInputSlot(3, 3).addIngredients(VanillaTypes.ITEM_STACK, eggStacks);
    }

    // output
    builder.addOutputSlot(76, 11).addItemStack(recipe.getOutput()).setOutputSlotBackground();
  }

  @Override
  public ResourceLocation getRegistryName(SeveringRecipe recipe) {
    return recipe.getId();
  }
}

