package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

public class SlimeTree {

  public static TreeGrower getGrower(FoliageType type) {
    return new TreeGrower("slime_tree_" + type.getSerializedName(),
      java.util.Optional.empty(),
      java.util.Optional.of(switch (type) {
        case EARTH -> TinkerStructures.earthSlimeTree;
        case SKY -> TinkerStructures.skySlimeTree;
        case ENDER -> TinkerStructures.enderSlimeTree;
        case BLOOD -> TinkerStructures.bloodSlimeFungus;
        case ICHOR -> TinkerStructures.ichorSlimeFungus;
      }),
      java.util.Optional.empty()
    );
  }

  

  
}
