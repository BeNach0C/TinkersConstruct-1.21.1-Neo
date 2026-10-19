package slimeknights.tconstruct.library.utils;

import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ResourceId implements Comparable<ResourceId> {
  private final ResourceLocation id;

  @Override
  public int compareTo(ResourceId o) {
    return this.id.compareTo(o.id);
  }

  protected ResourceId(String namespace, String path) {
    this.id = ResourceLocation.fromNamespaceAndPath(namespace, path);
  }

  public ResourceId(ResourceLocation location) {
    this.id = location;
  }

  public ResourceId(String location) {
    this.id = ResourceLocation.parse(location);
  }

  public ResourceLocation getId() {
    return id;
  }

  public String getNamespace() {
    return id.getNamespace();
  }

  public String getPath() {
    return id.getPath();
  }

  @Override
  public String toString() {
    return id.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof ResourceId other) {
      return this.id.equals(other.id);
    }
    if (obj instanceof ResourceLocation rl) {
      return this.id.equals(rl);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Nullable
  protected static <T extends ResourceId> T tryParse(String string, BiFunction<String,String,T> constructor) {
    int i = string.indexOf(':');
    if (i >= 0) {
      return tryBuild(string.substring(0, i), string.substring(i + 1), constructor);
    }
    return tryBuild("minecraft", string, constructor);
  }

  @Nullable
  protected static <T extends ResourceId> T tryBuild(String namespace, String path, BiFunction<String,String,T> constructor) {
    if (ResourceLocation.isValidNamespace(namespace) && ResourceLocation.isValidPath(path)) {
      return constructor.apply(namespace, path);
    }
    return null;
  }
}
