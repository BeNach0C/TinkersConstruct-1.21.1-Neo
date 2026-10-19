package slimeknights.tconstruct.smeltery.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.TConstruct;

/**
 * Packet for when the fluid changes in a block entity.
 */
public class FluidUpdatePacket implements IThreadsafePacket {
  public static final Type<FluidUpdatePacket> ID = new Type<>(TConstruct.getResource("fluid_update"));
  public static final StreamCodec<RegistryFriendlyByteBuf, FluidUpdatePacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), FluidUpdatePacket::new);

  protected final BlockPos pos;
  protected final FluidStack fluid;

  public FluidUpdatePacket(BlockPos pos, FluidStack fluid) {
    this.pos = pos;
    this.fluid = fluid;
  }

  public FluidUpdatePacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.fluid = FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluid);
  }

  public BlockPos pos() {
    return pos;
  }

  @Override
  public Type<FluidUpdatePacket> type() {
    return ID;
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    Level level = context.player().level();
    if (level != null && level.isLoaded(pos)) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof IFluidPacketReceiver receiver) {
        receiver.updateFluidTo(fluid);
      }
    }
  }

  /** Interface to implement for anything wishing to receive fluid updates */
  public interface IFluidPacketReceiver {
    /**
     * Updates the current fluid to the specified value
     * @param fluid New fluidstack
     */
    void updateFluidTo(FluidStack fluid);
  }
}

