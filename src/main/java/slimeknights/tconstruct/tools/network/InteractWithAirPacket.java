package slimeknights.tconstruct.tools.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/** Packet sent client to server when an empty hand interaction */
public enum InteractWithAirPacket implements slimeknights.mantle.network.packet.IThreadsafePacket {
  /** Right click with an empty main hand and a chestplate */
  MAINHAND(InteractionHand.MAIN_HAND),
  /** Right click with an empty off hand and a chestplate */
  OFFHAND(InteractionHand.OFF_HAND),
  /** Left click with a supported tool */
  LEFT_CLICK(InteractionHand.MAIN_HAND);
  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<InteractWithAirPacket> TYPE =
    new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct", "interact_with_air"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, InteractWithAirPacket> STREAM_CODEC =
    net.minecraft.network.codec.StreamCodec.of((buf, packet) -> packet.encode(buf), InteractWithAirPacket::read);

  private final InteractionHand hand;

  InteractWithAirPacket(InteractionHand hand) {
      this.hand = hand;
  }

  /** Gets the packet for the given hand */
  public static InteractWithAirPacket fromChestplate(InteractionHand hand) {
    return hand == InteractionHand.OFF_HAND ? OFFHAND : MAINHAND;
  }

  /** Gets the packet from the packet buffer */
  public static InteractWithAirPacket read(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    return buffer.readEnum(InteractWithAirPacket.class);
  }

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<InteractWithAirPacket> type() {
    return TYPE;
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    buffer.writeEnum(this);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player && !player.isSpectator()) {
      if (this == LEFT_CLICK) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(TinkerTags.Items.INTERACTABLE_LEFT)) {
          InteractionResult result = InteractionHandler.onLeftClickInteraction(player, held, hand);
          if (result.shouldSwing()) {
            player.swing(hand, true);
          }
        }
      } else {
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.is(TinkerTags.Items.INTERACTABLE_ARMOR) && player.getItemInHand(hand).isEmpty()) {
          InteractionResult result = InteractionHandler.onChestplateUse(player, chestplate, hand);
          if (result.shouldSwing()) {
            player.swing(hand, true);
          }
        }
      }
    }
  }
}
