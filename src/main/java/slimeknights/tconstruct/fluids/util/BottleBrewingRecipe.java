package slimeknights.tconstruct.fluids.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

/** Recipe for transforming a bottle, depending on a vanilla brewing recipe to get the ingredient */
public class BottleBrewingRecipe extends BrewingRecipe {
  private final Item from;
  private final Item to;
  private final Ingredient ingredientFallback;

  public BottleBrewingRecipe(Ingredient input, Item from, Item to, ItemStack output, Ingredient ingredientFallback) {
    super(input, ingredientFallback, output);
    this.from = from;
    this.to = to;
    this.ingredientFallback = ingredientFallback;
  }

  @Override
  public boolean isIngredient(ItemStack stack) {
    return ingredientFallback.test(stack);
  }

  @Override
  public Ingredient getIngredient() {
    return ingredientFallback;
  }
}
