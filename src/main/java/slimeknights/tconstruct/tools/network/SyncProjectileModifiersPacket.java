package slimeknights.tconstruct.tools.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.data.loadable.Streamable;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Objects;

public record SyncProjectileModifiersPacket(int entityId, ModifierNBT modifiers, CompoundTag persistentData) implements slimeknights.mantle.network.packet.IThreadsafePacket {
  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<SyncProjectileModifiersPacket> ID =
    new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(slimeknights.tconstruct.TConstruct.getResource("sync_projectile_modifiers"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, SyncProjectileModifiersPacket> CODEC =
    net.minecraft.network.codec.StreamCodec.of((buf, packet) -> packet.encode(buf), SyncProjectileModifiersPacket::new);

  @Override
  public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<SyncProjectileModifiersPacket> type() {
    return ID;
  }

  private static final Streamable<ModifierNBT> MODIFIER_LIST = ModifierEntry.LOADABLE.list(0).flatXmap(ModifierNBT::new, ModifierNBT::getModifiers);

  public SyncProjectileModifiersPacket(Entity entity) {
    this(entity.getId(), EntityModifierCapability.getOrEmpty(entity), PersistentDataCapability.getOrWarn(entity).getCopy());
  }

  public SyncProjectileModifiersPacket(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    this(buffer.readVarInt(), MODIFIER_LIST.decode(buffer), Objects.requireNonNullElse(buffer.readNbt(), new CompoundTag()));
  }

  public SyncProjectileModifiersPacket(FriendlyByteBuf buffer) {
    this(buffer.readVarInt(), MODIFIER_LIST.decode(buffer), Objects.requireNonNullElse(buffer.readNbt(), new CompoundTag()));
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(entityId);
    MODIFIER_LIST.encode(buffer, modifiers);
    buffer.writeNbt(persistentData);
  }

  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(entityId);
    MODIFIER_LIST.encode(buffer, modifiers);
    buffer.writeNbt(persistentData);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    Level level = SafeClientAccess.getLevel();
    if (level != null) {
      Entity entity = level.getEntity(entityId);
      if (entity != null) {
        EntityModifierCapability.getCapability(entity).setModifiers(modifiers);
        PersistentDataCapability.getOrWarn(entity).copyFrom(persistentData);
      }
    }
  }
}
