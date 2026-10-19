package slimeknights.tconstruct.smeltery.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;

import javax.annotation.Nullable;

/**
 * Packet to tell a multiblock to render a specific position as the cause of the error
 */
public class StructureErrorPositionPacket implements BlockEntityPacket<HeatingStructureBlockEntity> {
  public static final CustomPacketPayload.Type<StructureErrorPositionPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "structure_error_position"));
  public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, StructureErrorPositionPacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(StructureErrorPositionPacket::encode, StructureErrorPositionPacket::new);

  private final BlockPos controllerPos;
  @Nullable
  private final BlockPos errorPos;

  public StructureErrorPositionPacket(BlockPos controllerPos, @Nullable BlockPos errorPos) {
    this.controllerPos = controllerPos;
    this.errorPos = errorPos;
  }

  public StructureErrorPositionPacket(RegistryFriendlyByteBuf buffer) {
    this.controllerPos = buffer.readBlockPos();
    if (buffer.readBoolean()) {
      this.errorPos = buffer.readBlockPos();
    } else {
      this.errorPos = null;
    }
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(controllerPos);
    if (errorPos != null) {
      buffer.writeBoolean(true);
      buffer.writeBlockPos(errorPos);
    } else {
      buffer.writeBoolean(false);
    }
  }

  @Override
  public BlockPos pos() {
    return controllerPos;
  }

  @Override
  public Class<HeatingStructureBlockEntity> blockEntityType() {
    return HeatingStructureBlockEntity.class;
  }

  @Override
  public CustomPacketPayload.Type<StructureErrorPositionPacket> type() {
    return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "structure_error_position"));
  }

  @Override
  public void handleBlockEntity(IPayloadContext context, HeatingStructureBlockEntity be) {
    be.setErrorPos(errorPos);
  }
}
