package slimeknights.tconstruct.plugin.jei.partbuilder;

import lombok.NoArgsConstructor;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class PatternIngredientRenderer implements IIngredientRenderer<Pattern> {
  public static final PatternIngredientRenderer INSTANCE = new PatternIngredientRenderer();

  @Override
  public void render(GuiGraphics graphics, Pattern ingredient) {
    render(graphics, ingredient, 0, 0);
  }

  @Override
  public void render(GuiGraphics graphics, @Nullable Pattern pattern, int posX, int posY) {
    if (pattern != null) {
      GuiUtil.renderPattern(graphics, pattern, posX, posY);
    }
  }



  @Override
  public List<Component> getTooltip(Pattern pattern, TooltipFlag flag) {
    List<Component> list = new java.util.ArrayList<>();
    list.add(pattern.getDisplayName());
    if (flag.isAdvanced()) {
      list.add(Component.literal(pattern.toString()).withStyle(ChatFormatting.DARK_GRAY));
    }
    return list;
  }

  @Override
  public void getTooltip(ITooltipBuilder tooltip, Pattern pattern, TooltipFlag flag) {
    tooltip.add(pattern.getDisplayName());
    if (flag.isAdvanced()) {
      tooltip.add(Component.literal(pattern.toString()).withStyle(ChatFormatting.DARK_GRAY));
    }
  }
}
