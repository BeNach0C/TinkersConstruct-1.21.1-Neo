package slimeknights.tconstruct.smeltery.menu;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringBaseContainerMenu;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Container for a block with a single item inventory
 */
public class SingleItemContainerMenu extends TriggeringBaseContainerMenu<BlockEntity> {
  public SingleItemContainerMenu(int id, @Nullable Inventory inv, @Nullable BlockEntity te) {
    super(TinkerSmeltery.singleItemContainer.get(), id, inv, te);
    if (te != null) {
      net.neoforged.neoforge.items.IItemHandler handler = te.getLevel().getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, te.getBlockPos(), te.getBlockState(), te, null);
      if (handler != null) this.addSlot(new SmartItemHandlerSlot(handler, 0, 80, 20));
      this.addInventorySlots();
    }
  }

  public SingleItemContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, BlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
