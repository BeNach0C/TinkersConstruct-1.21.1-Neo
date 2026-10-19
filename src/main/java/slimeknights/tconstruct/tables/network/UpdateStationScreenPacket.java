package slimeknights.tconstruct.tables.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.client.inventory.BaseTabbedScreen;

public class UpdateStationScreenPacket implements IThreadsafePacket {
  public static final UpdateStationScreenPacket INSTANCE = new UpdateStationScreenPacket();
  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<UpdateStationScreenPacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct", "update_station_screen"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, UpdateStationScreenPacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.unit(INSTANCE);

  private UpdateStationScreenPacket() {}

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf packetBuffer) {}

  @Override public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<UpdateStationScreenPacket> type() { return TYPE; }
  @Override
  public void handleThreadsafe(IPayloadContext context) {
    HandleClient.handle();
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle() {
      Screen screen = Minecraft.getInstance().screen;
      if (screen != null) {
        if (screen instanceof BaseTabbedScreen) {
          ((BaseTabbedScreen<?,?>) screen).updateDisplay();
        }
      }
    }
  }
}
