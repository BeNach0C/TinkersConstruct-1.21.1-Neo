package slimeknights.tconstruct.tools.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.modifiers.fluid.block.MoveBlocksFluidEffect;

/** Packet handling {@link MoveBlocksFluidEffect} syncing to the client */
public record PushBlockRowPacket(BlockPos pos, Direction direction, boolean push, int moving) implements slimeknights.mantle.network.packet.IThreadsafePacket {
  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<PushBlockRowPacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(slimeknights.tconstruct.TConstruct.getResource("push_block_row"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, PushBlockRowPacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(PushBlockRowPacket::encode, PushBlockRowPacket::new);

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<PushBlockRowPacket> type() {
    return TYPE;
  }

  public PushBlockRowPacket(FriendlyByteBuf buffer) {
    this(buffer.readBlockPos(), buffer.readEnum(Direction.class), buffer.readBoolean(), buffer.readVarInt());
  }

  /** Gets the facing value for this packet */
  private Direction facing() {
    return push ? direction : direction.getOpposite();
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeEnum(direction);
    buffer.writeBoolean(push);
    buffer.writeVarInt(moving);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    HandleClient.handle(this);
  }

  /** Accesses client only safely */
  private static class HandleClient {
    public static void handle(PushBlockRowPacket packet) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
        MoveBlocksFluidEffect.moveBlocks(level, packet.pos, level.getBlockState(packet.pos), packet.facing(), packet.direction, packet.moving);
      }
    }
  }
}
