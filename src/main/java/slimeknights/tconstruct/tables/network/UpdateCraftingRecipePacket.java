package slimeknights.tconstruct.tables.network;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.BlockEntityPacket;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;

/**
 * Packet to send the current crafting recipe to a player who opens the crafting station.
 * TODO 1.21: make record.
 */
public class UpdateCraftingRecipePacket implements BlockEntityPacket<CraftingStationBlockEntity> {
  public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<UpdateCraftingRecipePacket> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct", "update_crafting_recipe"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, UpdateCraftingRecipePacket> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.ofMember(UpdateCraftingRecipePacket::encode, UpdateCraftingRecipePacket::new);

  private final BlockPos pos;
  private final ResourceLocation recipe;
  public UpdateCraftingRecipePacket(BlockPos pos, ResourceLocation recipe) {
    this.pos = pos;
    this.recipe = recipe;
  }

  public UpdateCraftingRecipePacket(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.recipe = buffer.readResourceLocation();
  }

  @Override
  public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeResourceLocation(recipe);
  }

  @Override
  public BlockPos pos() {
    return pos;
  }

    public Class<CraftingStationBlockEntity> blockEntityType() { return CraftingStationBlockEntity.class; }
  @Override public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<UpdateCraftingRecipePacket> type() { return TYPE; }
  @Override
  public void handleBlockEntity(IPayloadContext context, CraftingStationBlockEntity be) {
    Level level = be.getLevel();
    assert level != null;
    CraftingRecipe recipe = RecipeHelper.getRecipe(level.getRecipeManager(), this.recipe, CraftingRecipe.class).orElse(null);
    if (recipe != null) {
      be.updateRecipe(recipe);
    } else {
      TConstruct.LOG.error("Failed to update Crafting Station Recipe at {}: unknown recipe {}", pos, this.recipe);
    }
  }
}
