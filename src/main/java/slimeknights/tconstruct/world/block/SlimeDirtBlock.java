package slimeknights.tconstruct.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import slimeknights.tconstruct.world.TinkerWorld;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SlimeDirtBlock extends Block {

  public SlimeDirtBlock(Properties properties) {
    super(properties);
  }

  public net.neoforged.neoforge.common.util.TriState canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, BlockState plant) { return net.neoforged.neoforge.common.util.TriState.DEFAULT; }
}
