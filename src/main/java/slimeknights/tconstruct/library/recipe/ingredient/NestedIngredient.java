package slimeknights.tconstruct.library.recipe.ingredient;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import javax.annotation.Nullable;

/** Ingredient that contains another ingredient nested inside */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NestedIngredient implements ICustomIngredient {
  protected final Ingredient nested;


  /* Defer to nested */

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && nested.test(stack);
  }

  @Override
  public java.util.stream.Stream<ItemStack> getItems() {
    return java.util.Arrays.stream(nested.getItems());
  }

  @Override
  public boolean isSimple() {
    return nested.isSimple();
  }
}
