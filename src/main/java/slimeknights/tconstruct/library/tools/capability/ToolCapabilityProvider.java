package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Capability provider for tool stacks. In NeoForge 1.21 capabilities are no longer attached per stack,
 * so providers are created on demand when a capability is queried, and all known capabilities are registered in {@link RegisterCapabilitiesEvent}.
 */
public class ToolCapabilityProvider {
  private static final List<BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider>> PROVIDER_CONSTRUCTORS = new ArrayList<>();
  /** All capabilities that tool providers may return */
  private static final Set<ItemCapability<?,Void>> CAPABILITIES = new LinkedHashSet<>();

  private ToolCapabilityProvider() {}

  /** Gets the given capability for the passed tool stack */
  @Nullable
  public static <T> T getCapability(ItemStack stack, ItemCapability<T,Void> cap) {
    ToolStack tool = ToolStack.from(stack);
    Supplier<ToolStack> supplier = () -> tool;
    for (BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider> constructor : PROVIDER_CONSTRUCTORS) {
      IToolCapabilityProvider provider = constructor.apply(stack, supplier);
      if (provider != null) {
        T result = provider.getCapability(tool, cap);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  /** Registers a tool capability provider constructor. Every capability query will call this constructor to create your provider.
   * Is it valid for this constructor to return null. */
  public static void register(BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider> constructor) {
    PROVIDER_CONSTRUCTORS.add(constructor);
  }

  /** Registers a capability that tool providers may return, so it gets hooked into {@link RegisterCapabilitiesEvent} */
  public static void registerCapability(ItemCapability<?,Void> capability) {
    CAPABILITIES.add(Objects.requireNonNull(capability));
  }

  /** Registers all tool capabilities for the given items */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void registerItems(RegisterCapabilitiesEvent event, Item... items) {
    for (ItemCapability<?,Void> cap : CAPABILITIES) {
      ItemCapability raw = cap;
      event.registerItem(raw, (stack, context) -> getCapability((ItemStack)stack, raw), items);
    }
  }

  /** Interface to get a capability on a tool */
  @FunctionalInterface
  public interface IToolCapabilityProvider {
    /** Gets a capability on the given tool, or null if not supported */
    @Nullable
    <T> T getCapability(IToolStackView tool, ItemCapability<T,Void> cap);

    /** Called to clear the cache of the provider */
    default void clearCache() {}
  }
}

