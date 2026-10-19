package slimeknights.tconstruct.smeltery.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

/**
 * Packet for when the flowing state changes on a channel side.
 */
public class ChannelFlowPacket implements BlockEntityPacket<ChannelBlockEntity> {
  public static final CustomPacketPayload.Type<ChannelFlowPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "channel_flow"));
  public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, ChannelFlowPacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(ChannelFlowPacket::encode, ChannelFlowPacket::new);

  private final BlockPos pos;
  private final Direction side;
  private final boolean flow;

  public ChannelFlowPacket(BlockPos pos, Direction side, boolean flow) {
    this.pos = pos;
    this.side = side;
    this.flow = flow;
  }

  public ChannelFlowPacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.side = buffer.readEnum(Direction.class);
    this.flow = buffer.readBoolean();
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeEnum(side);
    buffer.writeBoolean(flow);
  }

  @Override
  public BlockPos pos() {
    return pos;
  }

  @Override
  public Class<ChannelBlockEntity> blockEntityType() {
    return ChannelBlockEntity.class;
  }

  @Override
  public CustomPacketPayload.Type<ChannelFlowPacket> type() {
    return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "channel_flow"));
  }

  @Override
  public void handleBlockEntity(IPayloadContext context, ChannelBlockEntity be) {
    be.setFlow(side, flow);
  }
}
