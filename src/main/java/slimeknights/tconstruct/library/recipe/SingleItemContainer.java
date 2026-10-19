package slimeknights.tconstruct.library.recipe;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.container.ISingleStackContainer;

/** Simple class for an inventory containing just one item */
public class SingleItemContainer implements ISingleStackContainer, net.minecraft.world.item.crafting.RecipeInput {
  public int size() { return 1; }
  @Override
  public ItemStack getItem(int index) { return index == 0 ? this.stack : ItemStack.EMPTY; }
  @Override
  public boolean isEmpty() { return this.stack.isEmpty(); }
  @Getter @Setter
  private ItemStack stack = ItemStack.EMPTY;
  public ItemStack getStack() { return this.stack; }
  public void setStack(ItemStack stack) { this.stack = stack; }
}
