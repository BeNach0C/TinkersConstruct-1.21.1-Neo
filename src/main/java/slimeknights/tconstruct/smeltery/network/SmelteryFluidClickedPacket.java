package slimeknights.tconstruct.smeltery.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;

/**
 * Packet sent when a fluid is clicked in the smeltery UI
 */
public class SmelteryFluidClickedPacket implements IThreadsafePacket {
  private final int index;

  public SmelteryFluidClickedPacket(int index) {
    this.index = index;
  }

  public SmelteryFluidClickedPacket(RegistryFriendlyByteBuf buffer) {
    this.index = buffer.readVarInt();
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(index);
  }

  public static final CustomPacketPayload.Type<SmelteryFluidClickedPacket> TYPE =
    new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tconstruct", "smeltery_fluid_clicked"));
  public static final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, SmelteryFluidClickedPacket> STREAM_CODEC =
    net.minecraft.network.codec.StreamCodec.of((buf, packet) -> packet.encode(buf), SmelteryFluidClickedPacket::new);

  @Override
  public CustomPacketPayload.Type<SmelteryFluidClickedPacket> type() {
    return TYPE;
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    if (context.player() instanceof ServerPlayer sender && !sender.isSpectator()) {
      AbstractContainerMenu container = sender.containerMenu;
      if (container instanceof BaseContainerMenu<?> base && base.getTile() instanceof ISmelteryTankHandler tank) {
        tank.getTank().moveFluidToBottom(index);
      }
    }
  }
}
