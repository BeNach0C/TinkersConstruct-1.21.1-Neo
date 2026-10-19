package slimeknights.tconstruct.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.network.PacketDistributor;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.materials.stats.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.materials.traits.UpdateMaterialTraitsPacket;
import slimeknights.tconstruct.library.modifiers.UpdateModifiersPacket;
import slimeknights.tconstruct.library.modifiers.fluid.UpdateFluidEffectsPacket;
import slimeknights.tconstruct.library.tools.definition.UpdateToolDefinitionDataPacket;
import slimeknights.tconstruct.library.tools.layout.UpdateTinkerSlotLayoutsPacket;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClickedPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryTankUpdatePacket;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;
import slimeknights.tconstruct.smeltery.network.StructureUpdatePacket;
import slimeknights.tconstruct.tables.network.StationTabPacket;
import slimeknights.tconstruct.tables.network.TinkerStationRenamePacket;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;
import slimeknights.tconstruct.tables.network.UpdateTinkerStationRecipePacket;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.network.InteractWithAirPacket;
import slimeknights.tconstruct.tools.network.PushBlockRowPacket;
import slimeknights.tconstruct.tools.network.SyncProjectileModifiersPacket;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;
import slimeknights.tconstruct.tools.network.ToolContainerFluidUpdatePacket;

import javax.annotation.Nullable;

/**
 * Base network class for all tinkers logic
 * <p>
 * In general, if you need to send packets you should use your own network class
 */
public class TinkerNetwork extends NetworkWrapper {
  private static TinkerNetwork instance = null;

  private TinkerNetwork() {
    super(TConstruct.getResource("network"), "5");
  }

  /** Gets the instance of the network */
  public static TinkerNetwork getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Attempt to call network getInstance before network is setup");
    }
    return instance;
  }

  /**
   * Called during mod construction to setup the network.
   */
  public static void setup() {
    if (instance != null) {
      return;
    }
    instance = new TinkerNetwork();
    registerPackets();
  }

  /**
   * Registers all tinkers packets with NeoForge payload handlers.
   */
  public static void registerPackets() {
    TinkerNetwork net = getInstance();

    // Clientbound packets (playToClient)
    net.playToClient().registerPacket(UpdateMaterialsPacket.ID, UpdateMaterialsPacket.CODEC);
    net.playToClient().registerPacket(UpdateMaterialStatsPacket.ID, UpdateMaterialStatsPacket.CODEC);
    net.playToClient().registerPacket(UpdateMaterialTraitsPacket.ID, UpdateMaterialTraitsPacket.CODEC);
    net.playToClient().registerPacket(UpdateModifiersPacket.ID, UpdateModifiersPacket.CODEC);
    net.playToClient().registerPacket(UpdateFluidEffectsPacket.ID, UpdateFluidEffectsPacket.CODEC);
    net.playToClient().registerPacket(UpdateToolDefinitionDataPacket.ID, UpdateToolDefinitionDataPacket.CODEC);
    net.playToClient().registerPacket(UpdateTinkerSlotLayoutsPacket.ID, UpdateTinkerSlotLayoutsPacket.CODEC);
    net.playToClient().registerPacket(InventorySlotSyncPacket.ID, InventorySlotSyncPacket.CODEC);
    net.playToClient().registerPacket(SyncPersistentDataPacket.ID, SyncPersistentDataPacket.CODEC);
    net.playToClient().registerPacket(UpdateNeighborsPacket.ID, UpdateNeighborsPacket.CODEC);
    net.playToClient().registerPacket(GeneratePartTexturesPacket.ID, GeneratePartTexturesPacket.CODEC);
    net.playToClient().registerPacket(FluidUpdatePacket.ID, FluidUpdatePacket.CODEC);
    net.playToClient().registerPacket(SyncProjectileModifiersPacket.ID, SyncProjectileModifiersPacket.CODEC);
    net.playToClient().registerPacket(StructureErrorPositionPacket.TYPE, StructureErrorPositionPacket.STREAM_CODEC);
    net.playToClient().registerPacket(StructureUpdatePacket.TYPE, StructureUpdatePacket.STREAM_CODEC);
    net.playToClient().registerPacket(SmelteryTankUpdatePacket.TYPE, SmelteryTankUpdatePacket.STREAM_CODEC);
    net.playToClient().registerPacket(ChannelFlowPacket.TYPE, ChannelFlowPacket.STREAM_CODEC);
    net.playToClient().registerPacket(FaucetActivationPacket.TYPE, FaucetActivationPacket.STREAM_CODEC);
    net.playToClient().registerPacket(UpdateStationScreenPacket.TYPE, UpdateStationScreenPacket.STREAM_CODEC);
    net.playToClient().registerPacket(UpdateTinkerStationRecipePacket.TYPE, UpdateTinkerStationRecipePacket.STREAM_CODEC);
    net.playToClient().registerPacket(UpdateCraftingRecipePacket.TYPE, UpdateCraftingRecipePacket.STREAM_CODEC);
    net.playToClient().registerPacket(EntityMovementChangePacket.TYPE, EntityMovementChangePacket.STREAM_CODEC);
    net.playToClient().registerPacket(PushBlockRowPacket.TYPE, PushBlockRowPacket.STREAM_CODEC);
    net.playToClient().registerPacket(ToolContainerFluidUpdatePacket.TYPE, ToolContainerFluidUpdatePacket.STREAM_CODEC);

    // Serverbound packets (playToServer)
    net.playToServer().registerPacket(TinkerControlPacket.TYPE, TinkerControlPacket.STREAM_CODEC);
    net.playToServer().registerPacket(StationTabPacket.TYPE, StationTabPacket.STREAM_CODEC);
    net.playToServer().registerPacket(TinkerStationSelectionPacket.TYPE, TinkerStationSelectionPacket.STREAM_CODEC);
    net.playToServer().registerPacket(TinkerStationRenamePacket.TYPE, TinkerStationRenamePacket.STREAM_CODEC);
    net.playToServer().registerPacket(SmelteryFluidClickedPacket.TYPE, SmelteryFluidClickedPacket.STREAM_CODEC);
    net.playToServer().registerPacket(InteractWithAirPacket.TYPE, InteractWithAirPacket.STREAM_CODEC);
  }

  /**
   * Sends a vanilla packet to the given player
   * @param player  Player
   * @param packet  Packet
   */
  public void sendVanillaPacket(Entity player, Packet<?> packet) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(packet);
    }
  }

  /**
   * Same as {@link #sendToClientsAround(CustomPacketPayload, ServerLevel, BlockPos)}, but checks that the world is a serverworld
   * @param msg       Packet to send
   * @param world     World instance
   * @param position  Target position
   */
  public void sendToClientsAround(CustomPacketPayload msg, @Nullable LevelAccessor world, BlockPos position) {
    if (world instanceof ServerLevel server) {
      sendToClientsAround(msg, server, position);
    }
  }

  /**
   * Sends a packet to the whole player list
   * @param targetedPlayer  Main player to target, if null uses whole list
   * @param playerList      Player list to use if main player is null
   * @param msg             Message to send
   */
  public void sendToPlayerList(@Nullable ServerPlayer targetedPlayer, PlayerList playerList, CustomPacketPayload msg) {
    if (targetedPlayer != null) {
      sendTo(msg, targetedPlayer);
    } else {
      for (ServerPlayer player : playerList.getPlayers()) {
        sendTo(msg, player);
      }
    }
  }
}
