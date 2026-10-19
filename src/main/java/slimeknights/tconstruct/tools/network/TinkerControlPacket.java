package slimeknights.tconstruct.tools.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.logic.DoubleJumpHandler;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/**
 * Generic packet for various controls the client may send to the server
 */
public enum TinkerControlPacket implements slimeknights.mantle.network.packet.IThreadsafePacket {
  DOUBLE_JUMP,
  ANTIGRAVITY_JUMP,
  // helmet
  START_HELMET_INTERACT(TooltipKey.NORMAL),
  START_HELMET_INTERACT_SHIFT(TooltipKey.SHIFT),
  START_HELMET_INTERACT_CONTROL(TooltipKey.CONTROL),
  START_HELMET_INTERACT_ALT(TooltipKey.ALT),
  STOP_HELMET_INTERACT,
  // leggings
  START_LEGGINGS_INTERACT(TooltipKey.NORMAL),
  START_LEGGINGS_INTERACT_SHIFT(TooltipKey.SHIFT),
  START_LEGGINGS_INTERACT_CONTROL(TooltipKey.CONTROL),
  START_LEGGINGS_INTERACT_ALT(TooltipKey.ALT),
  STOP_LEGGINGS_INTERACT;

  private final TooltipKey modifier;
  private TinkerControlPacket(TooltipKey modifier) { this.modifier = modifier; }
  TinkerControlPacket() {
    this(TooltipKey.UNKNOWN);
  }

  /** Gets the packet for helmet interaction */
  public static TinkerControlPacket getStartHelmetInteract(TooltipKey key) {
    return switch (key) {
      case SHIFT -> START_HELMET_INTERACT_SHIFT;
      case CONTROL -> START_HELMET_INTERACT_CONTROL;
      case ALT -> START_HELMET_INTERACT_ALT;
      default -> START_HELMET_INTERACT;
    };
  }

  /** Gets the packet for leggings interaction */
  public static TinkerControlPacket getStartLeggingsInteract(TooltipKey key) {
    return switch (key) {
      case SHIFT -> START_LEGGINGS_INTERACT_SHIFT;
      case CONTROL -> START_LEGGINGS_INTERACT_CONTROL;
      case ALT -> START_LEGGINGS_INTERACT_ALT;
      default -> START_LEGGINGS_INTERACT;
    };
  }

  public static TinkerControlPacket read(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    return buffer.readEnum(TinkerControlPacket.class);
  }

  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<TinkerControlPacket> TYPE =
    new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct", "tinker_control"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, TinkerControlPacket> STREAM_CODEC =
    net.minecraft.network.codec.StreamCodec.of((buf, packet) -> packet.encode(buf), TinkerControlPacket::read);

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<TinkerControlPacket> type() {
    return TYPE;
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf packetBuffer) {
    packetBuffer.writeEnum(this);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      switch (this) {
        case DOUBLE_JUMP -> DoubleJumpHandler.extraJump(player);
        case ANTIGRAVITY_JUMP -> TinkerEffects.antigravity.get().antigravityJump(player);
        case START_HELMET_INTERACT, START_HELMET_INTERACT_SHIFT, START_HELMET_INTERACT_CONTROL, START_HELMET_INTERACT_ALT
          -> InteractionHandler.startArmorInteract(player, EquipmentSlot.HEAD, this.modifier);
        case STOP_HELMET_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.HEAD);
        case START_LEGGINGS_INTERACT, START_LEGGINGS_INTERACT_SHIFT, START_LEGGINGS_INTERACT_CONTROL, START_LEGGINGS_INTERACT_ALT
          -> InteractionHandler.startArmorInteract(player, EquipmentSlot.LEGS, this.modifier);
        case STOP_LEGGINGS_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.LEGS);
      }
    }
  }
}
