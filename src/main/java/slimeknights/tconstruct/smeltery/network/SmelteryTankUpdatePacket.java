package slimeknights.tconstruct.smeltery.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet sent when the smeltery fluids change
 */
public class SmelteryTankUpdatePacket implements BlockEntityPacket<ISmelteryTankHandler> {
  public static final CustomPacketPayload.Type<SmelteryTankUpdatePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "smeltery_tank_update"));
  public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, SmelteryTankUpdatePacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(SmelteryTankUpdatePacket::encode, SmelteryTankUpdatePacket::new);

  private final BlockPos pos;
  private final List<FluidStack> fluids;

  public SmelteryTankUpdatePacket(BlockPos pos, List<FluidStack> fluids) {
    this.pos = pos;
    this.fluids = fluids;
  }

  public SmelteryTankUpdatePacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    int size = buffer.readVarInt();
    this.fluids = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      this.fluids.add(FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer));
    }
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeVarInt(fluids.size());
    for (FluidStack fluid : fluids) {
      FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluid);
    }
  }

  @Override
  public BlockPos pos() {
    return pos;
  }

  @Override
  public Class<ISmelteryTankHandler> blockEntityType() {
    return ISmelteryTankHandler.class;
  }

  @Override
  public CustomPacketPayload.Type<SmelteryTankUpdatePacket> type() {
    return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "smeltery_tank_update"));
  }

  @Override
  public void handleBlockEntity(IPayloadContext context, ISmelteryTankHandler be) {
    be.updateFluidsFromPacket(fluids);
  }
}
