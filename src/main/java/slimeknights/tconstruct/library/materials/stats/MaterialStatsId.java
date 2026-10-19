package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.IMaterialUser;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.utils.IdParser;
import slimeknights.tconstruct.library.utils.ResourceId;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialStatsId extends ResourceId implements IMaterialUser {
  public static final IdParser<MaterialStatsId> PARSER = new IdParser<>(MaterialStatsId::new, "Material Stat Type");

  public MaterialStatsId(String text) {
    super(text);
  }

  public MaterialStatsId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialStatsId(ResourceLocation location) {
    super(location);
  }

  /** Checks if the given material can be used */
  @Override
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance().getMaterialStats(material, this).isPresent();
  }


  /** {@return Material Stats ID, or null if invalid} */
  @Nullable
  public static MaterialStatsId tryParse(String string) {
    return tryParse(string, MaterialStatsId::new);
  }

  /** {@return Material Stats ID, or null if invalid} */
  @Nullable
  public static MaterialStatsId tryBuild(String namespace, String path) {
    return tryBuild(namespace, path, MaterialStatsId::new);
  }

  public static final Object SERIALIZER = new com.google.gson.JsonDeserializer<MaterialStatsId>() {
    @Override
    public MaterialStatsId deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
      return new MaterialStatsId(json.getAsString());
    }
  };
}
