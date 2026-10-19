package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import slimeknights.mantle.fluid.texture.ClientTextureFluidType;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Objects;
import java.util.function.Consumer;

public class PotionFluidType extends FluidType {
  public PotionFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public String getDescriptionId(FluidStack stack) {
    PotionContents contents = stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    return Potion.getName(contents.potion(), "item.minecraft.potion.effect.");
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    itemStack.applyComponents(fluidStack.getComponentsPatch());
    return itemStack;
  }

  @Override
  public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    consumer.accept(new ClientTextureFluidType(this) {
      /**
       * Gets the color, based on {@link PotionContents#getColor(ItemStack)}
       * @param stack  Fluid stack instance
       * @return  Color for the fluid
       */
      @Override
      public int getTintColor(FluidStack stack) {
        PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
        if (contents != null) {
          if (contents.customColor().isPresent()) {
            return contents.customColor().get() | 0xFF000000;
          }
          if (!contents.hasEffects()) {
            return getTintColor();
          }
          return contents.getColor() | 0xFF000000;
        }
        return getTintColor();
      }
    });
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(net.minecraft.core.Holder<Potion> potion, int size) {
    FluidStack stack = new FluidStack(TinkerFluids.potion.get(), size);
    stack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new PotionContents(potion));
    return stack;
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(ResourceKey<Potion> potion, int size) {
    return potionFluid(BuiltInRegistries.POTION.getHolderOrThrow(potion), size);
  }

  /** Creates a fluid stack for the given potion */
  @SuppressWarnings("deprecation")
  public static FluidStack potionFluid(Potion potion, int size) {
    return potionFluid(BuiltInRegistries.POTION.wrapAsHolder(potion), size);
  }

  /** Creates a fluid output for the given potion */
  public static FluidOutput potionResult(net.minecraft.core.Holder<Potion> potion, int size) {
    return FluidOutput.fromStack(potionFluid(potion, size));
  }

  /** Creates a fluid output for the given potion */
  public static FluidOutput potionResult(Potion potion, int size) {
    return FluidOutput.fromStack(potionFluid(potion, size));
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(net.minecraft.core.Holder<Potion> potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    stack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new PotionContents(potion));
    return stack;
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(ResourceKey<Potion> potion) {
    return potionBucket(BuiltInRegistries.POTION.getHolderOrThrow(potion));
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(Potion potion) {
    return potionBucket(BuiltInRegistries.POTION.wrapAsHolder(potion));
  }
}
