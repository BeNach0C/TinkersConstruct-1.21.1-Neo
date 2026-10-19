package slimeknights.tconstruct.shared.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.Getter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

/** Particle data for a fluid particle */
public class FluidParticleData implements ParticleOptions {
  @Getter
  private final ParticleType<FluidParticleData> type;
  @Getter
  private final FluidStack fluid;

  public FluidParticleData(ParticleType<FluidParticleData> type, FluidStack fluid) {
    this.type = type;
    this.fluid = fluid;
  }

  public FluidParticleData(FluidStack fluid) {
    this(slimeknights.tconstruct.shared.TinkerCommons.fluidParticle.get(), fluid);
  }

  @Override
  public ParticleType<FluidParticleData> getType() {
    return this.type;
  }

  /** Particle type for a fluid particle */
  public static class Type extends ParticleType<FluidParticleData> {
    public Type() {
      super(false);
    }

    @Override
    public MapCodec<FluidParticleData> codec() {
      return FluidStack.CODEC.xmap(fluid -> new FluidParticleData(this, fluid), data -> data.fluid).fieldOf("fluid");
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleData> streamCodec() {
      return FluidStack.STREAM_CODEC.map(fluid -> new FluidParticleData(this, fluid), data -> data.fluid);
    }
  }
}

