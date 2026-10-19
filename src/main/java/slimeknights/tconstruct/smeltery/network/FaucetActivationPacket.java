package slimeknights.tconstruct.smeltery.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;

/**
 * Sent to clients to activate the faucet animation clientside.
 */
public class FaucetActivationPacket implements BlockEntityPacket<FaucetBlockEntity> {
  public static final CustomPacketPayload.Type<FaucetActivationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "faucet_activation"));
  public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, FaucetActivationPacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(FaucetActivationPacket::encode, FaucetActivationPacket::new);

  protected final BlockPos pos;
  protected final FluidStack fluid;
  private final boolean isPouring;

  public FaucetActivationPacket(BlockPos pos, FluidStack fluid, boolean isPouring) {
    this.pos = pos;
    this.fluid = fluid;
    this.isPouring = isPouring;
  }

  public FaucetActivationPacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.fluid = FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer);
    this.isPouring = buffer.readBoolean();
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluid);
    buffer.writeBoolean(isPouring);
  }

  @Override
  public BlockPos pos() {
    return pos;
  }

  @Override
  public Class<FaucetBlockEntity> blockEntityType() {
    return FaucetBlockEntity.class;
  }

  @Override
  public CustomPacketPayload.Type<FaucetActivationPacket> type() {
    return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "faucet_activation"));
  }

  @Override
  public void handleBlockEntity(IPayloadContext context, FaucetBlockEntity be) {
    be.onActivationPacket(fluid, isPouring);
  }
}
