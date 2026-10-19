package slimeknights.tconstruct.smeltery.block.entity.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.mantle.block.entity.MantleBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Fuel module that supports multiple tanks, selecting just one for the fuel result */
public class MultitankFuelModule extends FuelModule implements IFluidHandler {
  /** Block position that will never be valid in world, used for sync */
  private static final BlockPos NULL_POS = new BlockPos(0, Short.MIN_VALUE, 0);

  /** Supplier for the list of valid tank positions */
  private final Supplier<List<BlockPos>> tankSupplier;
  /** Position of the last fluid handler */
  private BlockPos lastPos = NULL_POS;

  /** Map of all tank handlers at each relevant position. Used for fast switching between handlers, notably in the UI */
  private Map<BlockPos, IFluidHandler> tankHandlers;

  public MultitankFuelModule(MantleBlockEntity parent, Supplier<List<BlockPos>> tankSupplier) {
    super(parent);
    this.tankSupplier = tankSupplier;
  }

  /** Resets just the last fluid listener */
  private void clearLastListener() {
    super.resetHandler();
  }

  @Override
  protected void resetHandler() {
    this.lastPos = NULL_POS;
    super.resetHandler();
  }

  /** Called on structure rebuild to clear the gui handler list */
  public void clearFluidListeners() {
    tankHandlers = null;
  }

  /** Gets the map from position to fluid handler */
  private Map<BlockPos, IFluidHandler> getTankHandlers() {
    if (tankHandlers == null) {
      tankHandlers = new LinkedHashMap<>();
      Level world = getLevel();
      for (BlockPos pos : tankSupplier.get()) {
        IFluidHandler handler = world.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.UP);
        if (handler == null) {
          handler = world.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        }
        if (handler != null) {
          tankHandlers.put(pos, handler);
        }
      }
    }
    return tankHandlers;
  }


  /* Fuel finding */

  /**
   * Tries to consume fuel from the given position
   * @param pos  Position
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int tryFuelPosition(BlockPos pos, boolean consume) {
    IFluidHandler tankCap = getTankHandlers().get(pos);
    if (tankCap != null) {
      int temperature = tryLiquidFuel(tankCap, consume);
      if (temperature > 0) {
        clearLastListener();
        fluidHandler = tankCap;
        lastPos = pos;
        return temperature;
      }
    }
    return 0;
  }

  /**
   * Attempts to consume fuel from one of the tanks
   * @return  temperature of the found fluid, 0 if none
   */
  @Override
  public int findFuel(boolean consume) {
    if (fluidHandler != null) {
      int temperature = tryLiquidFuel(fluidHandler, consume);
      if (temperature > 0) {
        return temperature;
      }
    } else if (lastPos != NULL_POS) {
      int posTemp = tryFuelPosition(lastPos, consume);
      if (posTemp > 0) {
        return posTemp;
      }
    }

    // find a new handler among our tanks
    for (BlockPos pos : tankSupplier.get()) {
      if (!pos.equals(lastPos)) {
        int posTemp = tryFuelPosition(pos, consume);
        if (posTemp > 0) {
          return posTemp;
        }
      }
    }

    // no handler found, tell client of the lack of fuel
    if (consume) {
      temperature = 0;
      rate = 0;
    }
    return 0;
  }


  /* NBT */
  private static final String TAG_LAST_FUEL = "last_fuel";

  @Override
  public void readFromTag(CompoundTag nbt) {
    super.readFromTag(nbt);
    if (nbt.contains(TAG_LAST_FUEL)) {
      lastPos = NbtUtils.readBlockPos(nbt, TAG_LAST_FUEL).map(p -> p.offset(parent.getBlockPos())).orElse(NULL_POS);
    }
  }

  @Override
  public CompoundTag writeToTag(CompoundTag nbt) {
    nbt = super.writeToTag(nbt);
    if (lastPos != NULL_POS) {
      nbt.put(TAG_LAST_FUEL, NbtUtils.writeBlockPos(lastPos.subtract(parent.getBlockPos())));
    }
    return nbt;
  }


  /* UI syncing */
  private static final int LAST_X = 4;
  private static final int LAST_Y = 5;
  private static final int LAST_Z = 6;

  @Override
  public int getCount() {
    return 7;
  }

  @Override
  public int get(int index) {
    return switch (index) {
      case LAST_X -> lastPos.getX();
      case LAST_Y -> lastPos.getY();
      case LAST_Z -> lastPos.getZ();
      default -> super.get(index);
    };
  }

  @Override
  public void set(int index, int value) {
    if (LAST_X <= index && index <= LAST_Z) {
      switch (index) {
        case LAST_X -> lastPos = new BlockPos(value, lastPos.getY(), lastPos.getZ());
        case LAST_Y -> lastPos = new BlockPos(lastPos.getX(), value, lastPos.getZ());
        case LAST_Z -> lastPos = new BlockPos(lastPos.getX(), lastPos.getY(), value);
      }
      clearLastListener();
    } else {
      super.set(index, value);
    }
  }

  @Override
  public FuelInfo getFuelInfo() {
    if (fluidHandler == null && lastPos.getY() != NULL_POS.getY()) {
      IFluidHandler fluidCap = getTankHandlers().get(lastPos);
      if (fluidCap != null) {
        fluidHandler = fluidCap;
      }
    }

    if (fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) {
      for (IFluidHandler tankCap : getTankHandlers().values()) {
        if (!tankCap.getFluidInTank(0).isEmpty()) {
          clearLastListener();
          fluidHandler = tankCap;
          break;
        }
      }
    }

    FuelInfo info = super.getFuelInfo();
    if (!info.isEmpty()) {
      FluidStack currentFuel = info.getFluid();
      for (IFluidHandler handler : getTankHandlers().values()) {
        if (handler != fluidHandler) {
          FluidStack fluid = handler.getFluidInTank(0);
          if (fluid.isEmpty()) {
            info.add(0, handler.getTankCapacity(0));
          } else if (FluidStack.isSameFluidSameComponents(currentFuel, fluid)) {
            info.add(fluid.getAmount(), handler.getTankCapacity(0));
          }
        }
      }
    }

    return info;
  }


  /* Fluid handler */

  /** Gets the most recently used fluid */
  public FluidStack getLastFluid() {
    if (fluidHandler != null) {
      return fluidHandler.getFluidInTank(0);
    }
    BlockPos pos;
    if (lastPos.getY() != NULL_POS.getY()) {
      pos = lastPos;
    } else {
      List<BlockPos> positions = tankSupplier.get();
      if (!positions.isEmpty()) {
        pos = positions.get(0);
      } else {
        return FluidStack.EMPTY;
      }
    }
    IFluidHandler handler = getTankHandlers().get(pos);
    return handler != null ? handler.getFluidInTank(0) : FluidStack.EMPTY;
  }

  @Override
  public int getTanks() {
    return tankSupplier.get().size();
  }

  /** Gets the tank at the given index */
  private IFluidHandler getTank(int tank) {
    if (tank >= 0) {
      List<BlockPos> positions = tankSupplier.get();
      if (tank < positions.size()) {
        IFluidHandler handler = getTankHandlers().get(positions.get(tank));
        if (handler != null) {
          return handler;
        }
      }
    }
    return EmptyFluidHandler.INSTANCE;
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return getTank(tank).getFluidInTank(0);
  }

  @Override
  public int getTankCapacity(int tank) {
    return getTank(tank).getTankCapacity(0);
  }

  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return getTank(tank).isFluidValid(0, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    int totalFilled = 0;
    resource = resource.copy();
    for (IFluidHandler handler : getTankHandlers().values()) {
      int filled = handler.fill(resource, action);
      if (filled > 0) {
        totalFilled += filled;
        if (filled >= resource.getAmount()) {
          break;
        }
        if (totalFilled == filled) {
          resource = resource.copyWithAmount(resource.getAmount() - filled);
        } else {
          resource.shrink(filled);
        }
      }
    }
    return totalFilled;
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    for (IFluidHandler handler : getTankHandlers().values()) {
      FluidStack drained = handler.drain(resource, action);
      if (!drained.isEmpty()) {
        if (drainedSoFar.isEmpty()) {
          drainedSoFar = drained;
          if (drained.getAmount() >= resource.getAmount()) {
            break;
          }
          resource = resource.copyWithAmount(resource.getAmount() - drained.getAmount());
        } else {
          drainedSoFar.grow(drained.getAmount());
          resource.shrink(drained.getAmount());
          if (resource.isEmpty()) {
            break;
          }
        }
      }
    }
    return drainedSoFar;
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack drainedSoFar = FluidStack.EMPTY;
    FluidStack toDrain = FluidStack.EMPTY;
    for (IFluidHandler handler : getTankHandlers().values()) {
      if (toDrain.isEmpty()) {
        FluidStack drained = handler.drain(maxDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar = drained;
          if (drained.getAmount() >= maxDrain) {
            break;
          }
          toDrain = drained.copyWithAmount(maxDrain - drained.getAmount());
        }
      } else {
        FluidStack drained = handler.drain(toDrain, action);
        if (!drained.isEmpty()) {
          drainedSoFar.grow(drained.getAmount());
          toDrain.shrink(drained.getAmount());
          if (toDrain.isEmpty()) {
            break;
          }
        }
      }
    }
    return drainedSoFar;
  }
}

