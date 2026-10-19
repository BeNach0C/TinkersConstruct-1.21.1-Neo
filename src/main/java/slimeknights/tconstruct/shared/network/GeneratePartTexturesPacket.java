package slimeknights.tconstruct.shared.network;
import net.minecraft.network.RegistryFriendlyByteBuf;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.shared.client.ClientGeneratePartTexturesCommand;

/** Packet to tell the client to generate tool textures */
@RequiredArgsConstructor
public class GeneratePartTexturesPacket implements IThreadsafePacket {
  public static final Type<GeneratePartTexturesPacket> ID = new Type<>(TConstruct.getResource("generate_part_textures"));
  public static final StreamCodec<RegistryFriendlyByteBuf, GeneratePartTexturesPacket> CODEC = StreamCodec.of((buf, packet) -> packet.encode(buf), GeneratePartTexturesPacket::new);

  @Override
  public Type<GeneratePartTexturesPacket> type() {
    return ID;
  }

  private final Operation operation;
  private final String modId;
  private final String materialPath;

  public GeneratePartTexturesPacket(RegistryFriendlyByteBuf buffer) {
    operation = buffer.readEnum(Operation.class);
    modId = buffer.readUtf(Short.MAX_VALUE);
    materialPath = buffer.readUtf(Short.MAX_VALUE);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeEnum(operation);
    buffer.writeUtf(modId);
    buffer.writeUtf(materialPath);
  }

  @Override
  public void handleThreadsafe(IPayloadContext context) {
    context.enqueueWork(() -> ClientGeneratePartTexturesCommand.generateTextures(operation, modId, materialPath));
  }

  public enum Operation { ALL, MISSING }
}
