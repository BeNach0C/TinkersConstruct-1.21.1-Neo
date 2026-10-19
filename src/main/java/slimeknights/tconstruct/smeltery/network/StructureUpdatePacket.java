package slimeknights.tconstruct.smeltery.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet sent when the smeltery or foundry structure changes
 */
public class StructureUpdatePacket implements BlockEntityPacket<HeatingStructureBlockEntity> {
  public static final CustomPacketPayload.Type<StructureUpdatePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "structure_update"));
  public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, StructureUpdatePacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(StructureUpdatePacket::encode, StructureUpdatePacket::new);

  private final BlockPos pos;
  private final BlockPos minPos;
  private final BlockPos maxPos;
  private final List<BlockPos> tanks;

  public StructureUpdatePacket(BlockPos pos, BlockPos minPos, BlockPos maxPos, List<BlockPos> tanks) {
    this.pos = pos;
    this.minPos = minPos;
    this.maxPos = maxPos;
    this.tanks = tanks;
  }

  public StructureUpdatePacket(RegistryFriendlyByteBuf buffer) {
    pos = buffer.readBlockPos();
    minPos = buffer.readBlockPos();
    maxPos = buffer.readBlockPos();
    int count = buffer.readVarInt();
    tanks = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      tanks.add(buffer.readBlockPos());
    }
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeBlockPos(minPos);
    buffer.writeBlockPos(maxPos);
    buffer.writeVarInt(tanks.size());
    for (BlockPos tank : tanks) {
      buffer.writeBlockPos(tank);
    }
  }

  @Override
  public BlockPos pos() {
    return pos;
  }

  @Override
  public Class<HeatingStructureBlockEntity> blockEntityType() {
    return HeatingStructureBlockEntity.class;
  }

  @Override
  public CustomPacketPayload.Type<StructureUpdatePacket> type() {
    return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "structure_update"));
  }

  @Override
  public void handleBlockEntity(IPayloadContext context, HeatingStructureBlockEntity be) {
    be.setStructureSize(minPos, maxPos, tanks);
  }
}
