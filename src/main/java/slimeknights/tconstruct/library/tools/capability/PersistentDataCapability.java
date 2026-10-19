package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.bus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.SyncPersistentDataPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Data attachment to store persistent NBT data on an entity. For players, this is automatically synced to the client on load, but not during gameplay.
 * Persists after death, will reassess if we need some data to not persist death
 */
public class PersistentDataCapability {
  private PersistentDataCapability() {}

  /** Shared attachment register for Tinkers entity data */
  public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TConstruct.MOD_ID);

  /** Attachment type */
  public static final Supplier<AttachmentType<ModDataNBT>> CAPABILITY = ATTACHMENTS.register("persistent_data", () -> AttachmentType.builder(() -> new ModDataNBT())
    .serialize(new IAttachmentSerializer<CompoundTag, ModDataNBT>() {
      @Override
      public ModDataNBT read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        return ModDataNBT.readFromNBT(tag);
      }

      @Nullable
      @Override
      public CompoundTag write(ModDataNBT attachment, HolderLookup.Provider provider) {
        CompoundTag tag = attachment.getCopy();
        return tag.isEmpty() ? null : tag;
      }
    })
    .copyOnDeath()
    .build());

  /** Modifiers attachment type */
  public static final Supplier<AttachmentType<EntityModifierCapability.Holder>> MODIFIERS = ATTACHMENTS.register("modifiers", () -> AttachmentType.builder(EntityModifierCapability.Holder::new)
    .serialize(new IAttachmentSerializer<ListTag, EntityModifierCapability.Holder>() {
      @Override
      public EntityModifierCapability.Holder read(IAttachmentHolder holder, ListTag tag, HolderLookup.Provider provider) {
        EntityModifierCapability.Holder data = new EntityModifierCapability.Holder();
        data.setModifiers(ModifierNBT.readFromNBT(tag));
        return data;
      }

      @Nullable
      @Override
      public ListTag write(EntityModifierCapability.Holder attachment, HolderLookup.Provider provider) {
        return attachment.getModifiers().isEmpty() ? null : attachment.getModifiers().serializeToNBT();
      }
    })
    .build());

  /** Gets the data for the given entity, creating it if missing */
  public static ModDataNBT get(Entity entity) {
    return entity.getData(CAPABILITY);
  }

  /** Gets the data or warns if its missing. Kept for API compatibility, attachments are always present. */
  public static ModDataNBT getOrWarn(Entity entity) {
    return get(entity);
  }

  private static boolean registered = false;

  /** Initializes the deferred register during mod construction */
  public static void init(net.neoforged.bus.api.IEventBus bus) {
    ATTACHMENTS.register(bus);
  }

  /** Registers listeners for this attachment */
  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, event -> sync(event.getEntity()));
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, event -> sync(event.getEntity()));
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, event -> sync(event.getEntity()));
  }

  /** Syncs the data to the given player */
  private static void sync(Player player) {
    TinkerNetwork.getInstance().sendTo(new SyncPersistentDataPacket(get(player).getCopy()), player);
  }
}
