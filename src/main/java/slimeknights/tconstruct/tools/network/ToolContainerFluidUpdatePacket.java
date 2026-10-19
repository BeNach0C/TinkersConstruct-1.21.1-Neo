package slimeknights.tconstruct.tools.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

/** Packet used when a fluid is changed inside a tool container menu */
public record ToolContainerFluidUpdatePacket(FluidStack fluid) implements slimeknights.mantle.network.packet.IThreadsafePacket {
  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<ToolContainerFluidUpdatePacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(slimeknights.tconstruct.TConstruct.getResource("tool_container_fluid_update"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ToolContainerFluidUpdatePacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(ToolContainerFluidUpdatePacket::encode, ToolContainerFluidUpdatePacket::new);

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<ToolContainerFluidUpdatePacket> type() {
    return TYPE;
  }

  public ToolContainerFluidUpdatePacket(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    this(FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer));
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, fluid);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    Player player = SafeClientAccess.getPlayer();
    if (player != null && player.containerMenu instanceof ToolContainerMenu toolMenu) {
      // toolMenu.getTank().setFluid(fluid);
    }
  }
}
