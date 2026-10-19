package slimeknights.tconstruct.library.fluid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;

/** Empty fluid handler item instance, usable like {@link EmptyFluidHandler#INSTANCE} */
public class EmptyFluidHandlerItem extends EmptyFluidHandler implements IFluidHandlerItem {
  public static final EmptyFluidHandlerItem INSTANCE = new EmptyFluidHandlerItem(ItemStack.EMPTY);

  public EmptyFluidHandlerItem(ItemStack container) {
    this.container = container;
  }

  /** Container reference */
  @Getter
  private final ItemStack container;
}
