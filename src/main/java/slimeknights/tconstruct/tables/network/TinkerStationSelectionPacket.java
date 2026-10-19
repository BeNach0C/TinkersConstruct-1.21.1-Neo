package slimeknights.tconstruct.tables.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

public class TinkerStationSelectionPacket implements IThreadsafePacket {
  private final ResourceLocation layoutName;
  public TinkerStationSelectionPacket(ResourceLocation layoutName) { this.layoutName = layoutName; }
  public TinkerStationSelectionPacket(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    this.layoutName = buffer.readResourceLocation();
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    buffer.writeResourceLocation(this.layoutName);
  }

  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<TinkerStationSelectionPacket> TYPE =
    new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct", "tinker_station_selection"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, TinkerStationSelectionPacket> STREAM_CODEC =
    net.minecraft.network.codec.StreamCodec.of((buf, packet) -> packet.encode(buf), TinkerStationSelectionPacket::new);

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<TinkerStationSelectionPacket> type() {
    return TYPE;
  }
  @Override
  public void handleThreadsafe(IPayloadContext context) {
    if (context.player() instanceof ServerPlayer sender) {
      AbstractContainerMenu container = sender.containerMenu;
      if (container instanceof TinkerStationContainerMenu tinker) {
        tinker.setToolSelection(StationSlotLayoutLoader.getInstance().get(layoutName));
      }
    }
  }
}
