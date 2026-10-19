package slimeknights.tconstruct.tables.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

/** Packet to send to the server to update the name in the UI */
public class TinkerStationRenamePacket implements IThreadsafePacket {
  private final String name;
  public TinkerStationRenamePacket(String name) { this.name = name; }

  public TinkerStationRenamePacket(net.minecraft.network.RegistryFriendlyByteBuf buf) {
    this.name = buf.readUtf(Short.MAX_VALUE);
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buf) {
    buf.writeUtf(name);
  }

  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<TinkerStationRenamePacket> TYPE =
    new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct", "tinker_station_rename"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, TinkerStationRenamePacket> STREAM_CODEC =
    net.minecraft.network.codec.StreamCodec.of((buf, packet) -> packet.encode(buf), TinkerStationRenamePacket::new);

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<TinkerStationRenamePacket> type() {
    return TYPE;
  }
  @Override
  public void handleThreadsafe(IPayloadContext context) {
    if (context.player() instanceof ServerPlayer sender && sender.containerMenu instanceof TinkerStationContainerMenu station) {
      TinkerStationBlockEntity tile = station.getTile();
      if (tile != null) {
        station.getTile().setItemName(name);
      }
    }
  }
}
