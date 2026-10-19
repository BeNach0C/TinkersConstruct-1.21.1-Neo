package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
  public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    // ensure the tag exists to help addons discover it, but we don't intend to use it
    tag(TinkerTags.Materials.SHINY);
    tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT)
      // ancient hide is deprecated, don't add it to new tools
      .add(MaterialIds.ancientHide.getId())
      // fiery is obtained through specific progression in TF, better to not add a progression bypass
      .addOptional(MaterialIds.fiery.getId());
    tag(TinkerTags.Materials.NETHER).add(
      // tier 1
      MaterialIds.wood.getId(), MaterialIds.flint.getId(), MaterialIds.rock.getId(), MaterialIds.bone.getId(),
      MaterialIds.leather.getId(), MaterialIds.string.getId(),
      // tier 2
      MaterialIds.gold.getId(), MaterialIds.slimewood.getId(),
      // tier 3
      MaterialIds.nahuatl.getId(), MaterialIds.obsidian.getId(), MaterialIds.darkthread.getId(), MaterialIds.steel.getId(),
      // tier 4
      MaterialIds.ancient.getId()
    ).addTag(TinkerTags.Materials.NETHER_GATED);

    // things that *require* nether access to craft
    tag(TinkerTags.Materials.NETHER_GATED).add(
      // tier 1
      MaterialIds.twistingVine.getId(), MaterialIds.weepingVine.getId(),
      // tier 2
      MaterialIds.scorchedStone.getId(), MaterialIds.necroticBone.getId(),
      // tier 3
      MaterialIds.cobalt.getId(),
      // tier 4
      MaterialIds.manyullyn.getId(), MaterialIds.hepatizon.getId(), MaterialIds.cinderslime.getId(),
      MaterialIds.queensSlime.getId(), MaterialIds.blazingBone.getId(), MaterialIds.blazewood.getId(),
      MaterialIds.jeweledHide.getId(), MaterialIds.jadeite.getId(),
      // ammo
      MaterialIds.glowstone.getId(), MaterialIds.ichor.getId(), MaterialIds.quartz.getId(), MaterialIds.blaze.getId(), MaterialIds.magma.getId()
    ).addOptional(MaterialIds.necronium.getId());

    // all materials to show in materials and you for ammo, kept to 9 options
    tag(TinkerTags.Materials.BASIC_AMMO).add(
      // head
      MaterialIds.flint.getId(), MaterialIds.wool.getId(), MaterialIds.glass.getId(),
      // shaft
      MaterialIds.wood.getId(), MaterialIds.bamboo.getId(), MaterialIds.cactus.getId(),
      // fletching
      MaterialIds.feather.getId(), MaterialIds.paper.getId(), MaterialIds.leaves.getId()
    );

    // tier 4 is split into several parts in different books
    tag(TinkerTags.Materials.BLAZING_BLOOD).add(
      MaterialIds.manyullyn.getId(), MaterialIds.hepatizon.getId(),
        MaterialIds.queensSlime.getId(), MaterialIds.cinderslime.getId(),
        MaterialIds.blazingBone.getId(), MaterialIds.blazewood.getId(),
        MaterialIds.jeweledHide.getId()
      ).addOptional(MaterialIds.nicrosil.getId());
    tag(TinkerTags.Materials.DISTANT).add(
      // tiers 1-2
      MaterialIds.chorus.getId(), MaterialIds.whitestone.getId(),
      // tier 4
      MaterialIds.knightmetal.getId(), MaterialIds.knightly.getId(), MaterialIds.knightslime.getId(), MaterialIds.enderslimeVine.getId(), MaterialIds.ancient.getId(),
      // ammo and maille
      MaterialIds.shulker.getId(), MaterialIds.dragonScale.getId(), MaterialIds.enderslime.getId(), MaterialIds.endRod.getId()
    ).addOptional(MaterialIds.ironwood.getId(), MaterialIds.steeleaf.getId(), MaterialIds.fiery.getId());

    // materials bartered by piglins
    tag(TinkerTags.Materials.BARTERED).add(
      // tier 3
      MaterialIds.nahuatl.getId(), MaterialIds.obsidian.getId(), MaterialIds.darkthread.getId(),
      MaterialIds.cobalt.getId(), MaterialIds.steel.getId(),
      // tier 4
      MaterialIds.manyullyn.getId(), MaterialIds.hepatizon.getId(),
      MaterialIds.cinderslime.getId(), MaterialIds.queensSlime.getId(),
      MaterialIds.blazingBone.getId(), MaterialIds.blazewood.getId(),
      MaterialIds.jeweledHide.getId(), MaterialIds.ancient.getId()
    ).addOptional(MaterialIds.necronium.getId());

    // tag all compat materials
    tag(TinkerTags.Materials.COMPATABILITY_METALS).addOptional(
      // tier 2
      MaterialIds.silver.getId(), MaterialIds.lead.getId(), MaterialIds.aluminum.getId(),
      MaterialIds.osmium.getId(), MaterialIds.ironwood.getId(),
      // tier 3
      MaterialIds.steeleaf.getId(),
      // tier 4
      MaterialIds.fiery.getId()
    ).addTag(TinkerTags.Materials.COMPATABILITY_BLOCKS);
    tag(TinkerTags.Materials.COMPATABILITY_BLOCKS).addTag(TinkerTags.Materials.COMPATABILITY_ALLOYS);
    tag(TinkerTags.Materials.COMPATABILITY_ALLOYS).addOptional(MaterialIds.bronze.getId(), MaterialIds.constantan.getId(), MaterialIds.invar.getId(), MaterialIds.electrum.getId(), MaterialIds.pewter.getId(), MaterialIds.nicrosil.getId());

    // material categories
    // melee harvest
    tag(TinkerTags.Materials.GENERAL).add(
      // tier 1
      MaterialIds.wood.getId(), MaterialIds.string.getId(), MaterialIds.vine.getId(), MaterialIds.leather.getId(),
      // tier 2
      MaterialIds.iron.getId(), MaterialIds.slimewood.getId(),
      // tier 3
      MaterialIds.slimesteel.getId(), MaterialIds.pigIron.getId(), MaterialIds.roseGold.getId(), MaterialIds.cobalt.getId(),
      // tier 4
      MaterialIds.cinderslime.getId(), MaterialIds.queensSlime.getId(), MaterialIds.enderslimeVine.getId()
    ).addOptional(
      // tier 1
      MaterialIds.treatedWood.getId(),
      // tier 2
      MaterialIds.osmium.getId(), MaterialIds.ironwood.getId(),
      // tier 3
      MaterialIds.platedSlimewood.getId(), MaterialIds.electrum.getId(), MaterialIds.steeleaf.getId(),
      // tier 4
      MaterialIds.fiery.getId()
    );
    tag(TinkerTags.Materials.HARVEST).add(
      // tier 1
      MaterialIds.rock.getId(), MaterialIds.copper.getId(),
      // tier 2
      MaterialIds.searedStone.getId(), MaterialIds.whitestone.getId(), MaterialIds.skyslimeVine.getId(), MaterialIds.twistingVine.getId(),
      // tier 3
      MaterialIds.amethystBronze.getId(),
      // tier 4
      MaterialIds.hepatizon.getId(), MaterialIds.jeweledHide.getId(), MaterialIds.knightslime.getId()
    ).addOptional(
      // tier 2
      MaterialIds.lead.getId(),
      // tier 3
      MaterialIds.bronze.getId(), MaterialIds.constantan.getId()
    );
    tag(TinkerTags.Materials.MELEE).add(
      // tier 1
      MaterialIds.flint.getId(), MaterialIds.bone.getId(), MaterialIds.chorus.getId(),
      // tier 2
      MaterialIds.scorchedStone.getId(), MaterialIds.necroticBone.getId(), MaterialIds.venombone.getId(), MaterialIds.weepingVine.getId(),
      // tier 3
      MaterialIds.nahuatl.getId(), MaterialIds.steel.getId(), MaterialIds.darkthread.getId(),
      // tier 4
      MaterialIds.manyullyn.getId(), MaterialIds.blazingBone.getId(), MaterialIds.knightmetal.getId()
    ).addOptional(
      // tier 2
      MaterialIds.silver.getId(),
      // tier 3
      MaterialIds.invar.getId(), MaterialIds.pewter.getId(), MaterialIds.necronium.getId(),
      // tier 4
      MaterialIds.nicrosil.getId()
    );

    // ranged
    tag(TinkerTags.Materials.BALANCED).add(
      // tier 1
      MaterialIds.wood.getId(), MaterialIds.chorus.getId(),
      MaterialIds.string.getId(), MaterialIds.vine.getId(), MaterialIds.leather.getId(),
      // tier 2
      MaterialIds.slimewood.getId(), MaterialIds.necroticBone.getId(),
      MaterialIds.skyslimeVine.getId(), MaterialIds.cheese.getId(),
      // tier 3
      MaterialIds.slimesteel.getId(), MaterialIds.darkthread.getId(), MaterialIds.cobalt.getId(), MaterialIds.pigIron.getId(),
      // tier 4
      MaterialIds.blazingBone.getId(), MaterialIds.jeweledHide.getId(), MaterialIds.enderslimeVine.getId()
    ).addOptional(
      // tier 1
      MaterialIds.treatedWood.getId(),
      // tier 2
      MaterialIds.silver.getId(), MaterialIds.ironwood.getId(),
      // tier 3
      MaterialIds.invar.getId(), MaterialIds.pewter.getId(), MaterialIds.steeleaf.getId()
    );
    tag(TinkerTags.Materials.LIGHT).add(
      // tier 1
      MaterialIds.bamboo.getId(), MaterialIds.bone.getId(),
      // tier 2
      MaterialIds.venombone.getId(), MaterialIds.twistingVine.getId(),
      // tier 3
      MaterialIds.nahuatl.getId(), MaterialIds.roseGold.getId(),
      // tier 4
      MaterialIds.hepatizon.getId(), MaterialIds.queensSlime.getId(), MaterialIds.knightslime.getId()
    ).addOptional(
      // tier 2
      MaterialIds.aluminum.getId(),
      // tier 3
      MaterialIds.necronium.getId(), MaterialIds.constantan.getId(), MaterialIds.platedSlimewood.getId(),
      // tier 4
      MaterialIds.nicrosil.getId()
    );
    tag(TinkerTags.Materials.HEAVY).add(
      // tier 1
      MaterialIds.copper.getId(), MaterialIds.cactus.getId(),
      // tier 2
      MaterialIds.iron.getId(), MaterialIds.weepingVine.getId(),
      // tier 3
      MaterialIds.amethystBronze.getId(), MaterialIds.steel.getId(),
      // tier 4
      MaterialIds.manyullyn.getId(), MaterialIds.cinderslime.getId(), MaterialIds.knightmetal.getId()
    ).addOptional(
      // tier 2
      MaterialIds.lead.getId(),
      // tier 3
      MaterialIds.bronze.getId(), MaterialIds.electrum.getId(),
      // tier 4
      MaterialIds.fiery.getId()
    );

    // slimeskull sort order
    tag(TinkerTags.Materials.SLIMESKULL).add(
      // creeper
      MaterialIds.gunpowder.getId(),
      // zombie
      MaterialIds.leather.getId(), MaterialIds.iron.getId(), MaterialIds.copper.getId(),
      // spider
      MaterialIds.string.getId(), MaterialIds.darkthread.getId(),
      // skeleton
      MaterialIds.bone.getId(), MaterialIds.ice.getId(), MaterialIds.necroticBone.getId(),
      // piglins
      MaterialIds.gold.getId(), MaterialIds.roseGold.getId(), MaterialIds.pigIron.getId(),
      // misc
      MaterialIds.blaze.getId(), MaterialIds.enderPearl.getId(), MaterialIds.dragonScale.getId(),
      // crafted
      MaterialIds.knightmetal.getId()
    );
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Tag Provider";
  }
}
