package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.AOE_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BLOCK_WHILE_CHARGING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BONUS_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_EXTRA_DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_FROSTSHIELD;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_OVERSLIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_REINFORCED;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_TANNED;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.COSMETIC_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CRAFTING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DAMAGE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DRILL_ATTACKS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DUAL_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_UPGRADE_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GEMS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HARVEST_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HARVEST_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HELMET_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HELMET_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.INTERACTION_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.INVISIBLE_INK_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.KNOCKBACK_SLINGS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.LEGGING_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.LEGGING_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELEE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELEE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELTING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.OVERSLIME_FRIEND;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.PROTECTION_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.REMOVE_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SECONDARY_DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SELF_KNOCKBACK_SLINGS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SEVERING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SHIELD_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLIME_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SMELTING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SPECIAL_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.TARGET_KNOCKBACK_SLINGS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.UPGRADES;

public class ModifierTagProvider extends AbstractModifierTagProvider {
  public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(GEMS).add(ModifierIds.diamond.getId(), ModifierIds.emerald.getId());
    tag(INVISIBLE_INK_BLACKLIST).add(
      TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.trim.getId(), TinkerModifiers.banner.getId(),
      TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId(),
      ModifierIds.shiny.getId(), TinkerModifiers.golden.getId()
    );
    tag(REMOVE_MODIFIER_BLACKLIST).add(TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId());
    tag(EXTRACT_MODIFIER_BLACKLIST).add(
      TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.trim.getId(), TinkerModifiers.banner.getId(),
      ModifierIds.rebalanced.getId(), TinkerModifiers.overslime.getId()
    ).addTag(REMOVE_MODIFIER_BLACKLIST);
    // blacklist modifiers that are not really slotless, they just have a slotless recipe
    tag(EXTRACT_SLOTLESS_BLACKLIST).add(ModifierIds.luck.getId(), ModifierIds.toolBelt.getId(), ModifierIds.featherFall.getId());
    // modifiers with different slot types per level
    tag(EXTRACT_UPGRADE_BLACKLIST).add(ModifierIds.leaping.getId(), ModifierIds.reflecting.getId(), ModifierIds.returning.getId());

    // modifiers in this tag support both left click and right click interaction
    tag(DUAL_INTERACTION).add(
      ModifierIds.bucketing.getId(), ModifierIds.splashing.getId(),
      ModifierIds.glowing.getId(), ModifierIds.firestarter.getId(),
      ModifierIds.stripping.getId(), ModifierIds.tilling.getId(), ModifierIds.pathing.getId(),
      ModifierIds.shears.getId(), ModifierIds.silkyShears.getId(),
      ModifierIds.harvest.getId(), ModifierIds.fishing.getId(),
      ModifierIds.slimeball.getId(), ModifierIds.sliver.getId(),
      ModifierIds.pockets.getId()
    );
    tag(BLOCK_WHILE_CHARGING).add(
      ModifierIds.flinging.getId(), ModifierIds.springing.getId(), ModifierIds.bonking.getId(), ModifierIds.warping.getId(),
      ModifierIds.spitting.getId(), ModifierIds.scope.getId(), ModifierIds.zoom.getId(), ModifierIds.brushing.getId(), ModifierIds.throwing.getId()
    );
    tag(SLIME_DEFENSE).add(
      ModifierIds.meleeProtection.getId(), ModifierIds.projectileProtection.getId(),
      ModifierIds.fireProtection.getId(), ModifierIds.magicProtection.getId(),
      ModifierIds.blastProtection.getId()
    );
    tag(OVERSLIME_FRIEND).add(
      ModifierIds.overgrowth.getId(), ModifierIds.overcast.getId(), ModifierIds.overburn.getId(), ModifierIds.overlord.getId(), ModifierIds.overshield.getId(), ModifierIds.overwield.getId(),
      ModifierIds.overforced.getId(), ModifierIds.overslimeFriend.getId(), TinkerModifiers.overworked.getId()
    );
    tag(AOE_INTERACTION).add(ModifierIds.pathing.getId(), ModifierIds.stripping.getId(), ModifierIds.tilling.getId(), ModifierIds.brushing.getId(), ModifierIds.splashing.getId(), ModifierIds.harvest.getId());
    tag(CHARGE_EMPTY_BOW_WITH_DRAWTIME).add(ModifierIds.flinging.getId(), ModifierIds.springing.getId(), ModifierIds.bonking.getId(), ModifierIds.warping.getId(), ModifierIds.throwing.getId());
    tag(CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME).add(ModifierIds.blocking.getId(), ModifierIds.scope.getId(), ModifierIds.zoom.getId(), ModifierIds.slurping.getId(), ModifierIds.tasty.getId(), ModifierIds.savory.getId(), ModifierIds.scrumptious.getId());
    tag(DRILL_ATTACKS).add(ModifierIds.flinging.getId(), ModifierIds.springing.getId(), ModifierIds.grapple.getId());
    tag(SELF_KNOCKBACK_SLINGS).add(ModifierIds.flinging.getId(), ModifierIds.springing.getId());
    tag(TARGET_KNOCKBACK_SLINGS).add(ModifierIds.bonking.getId());
    tag(KNOCKBACK_SLINGS).addTag(SELF_KNOCKBACK_SLINGS, TARGET_KNOCKBACK_SLINGS);

    // durability tags
    tag(BYPASS_TANNED).addTag(SECONDARY_DURABILITY);
    tag(SECONDARY_DURABILITY).add(
      // protection is used for the damage correction on armor, which tanned should prevent
      ModifierIds.protection.getId(),
      // counter-attack
      ModifierIds.thorns.getId(), ModifierIds.fiery.getId(), ModifierIds.freezing.getId(), ModifierIds.springy.getId(),
      ModifierIds.pierce.getId(), ModifierIds.venom.getId(), ModifierIds.conductive.getId(), ModifierIds.shock.getId(),
      // special effects
      ModifierIds.necrotic.getId(), ModifierIds.restore.getId(), TinkerModifiers.enderporting.getId()
    );
    tag(BYPASS_REINFORCED).add(ModifierIds.glowing.getId());
    tag(BYPASS_EXTRA_DURABILITY);
    tag(BYPASS_OVERSLIME).addTag(BYPASS_EXTRA_DURABILITY).add(ModifierIds.glowing.getId());
    tag(BYPASS_FROSTSHIELD).addTag(BYPASS_EXTRA_DURABILITY).add(ModifierIds.glowing.getId());

    // book tags
    this.tag(UPGRADES).addTag(GENERAL_UPGRADES, MELEE_UPGRADES, DAMAGE_UPGRADES, HARVEST_UPGRADES, ARMOR_UPGRADES, RANGED_UPGRADES);
    this.tag(ARMOR_UPGRADES).addTag(GENERAL_ARMOR_UPGRADES, HELMET_UPGRADES, CHESTPLATE_UPGRADES, LEGGING_UPGRADES, BOOT_UPGRADES);
    this.tag(ABILITIES).addTag(GENERAL_ABILITIES, INTERACTION_ABILITIES, MELEE_ABILITIES, HARVEST_ABILITIES, ARMOR_ABILITIES, RANGED_ABILITIES);
    this.tag(ARMOR_ABILITIES).addTag(GENERAL_ARMOR_ABILITIES, HELMET_ABILITIES, CHESTPLATE_ABILITIES, LEGGING_ABILITIES, BOOT_ABILITIES, SHIELD_ABILITIES);
    this.tag(DEFENSE).addTag(PROTECTION_DEFENSE, SPECIAL_DEFENSE);
    this.tag(SLOTLESS).addTag(GENERAL_SLOTLESS, BONUS_SLOTLESS, COSMETIC_SLOTLESS);

    // upgrades
    this.tag(GENERAL_UPGRADES).add(
      ModifierIds.diamond.getId(), ModifierIds.emerald.getId(), ModifierIds.netherite.getId(),
      ModifierIds.reinforced.getId(), ModifierIds.overforced.getId(), ModifierIds.soulbound.getId(),
      ModifierIds.experienced.getId(), ModifierIds.magnetic.getId(), ModifierIds.scope.getId(), ModifierIds.zoom.getId(),
      ModifierIds.tank.getId(), ModifierIds.smelting.getId(), ModifierIds.fireprimer.getId())
        .addOptional(ModifierIds.theOneProbe.getId());

    this.tag(MELEE_UPGRADES).add(
      ModifierIds.knockback.getId(), ModifierIds.padded.getId(),
      TinkerModifiers.severing.getId(), ModifierIds.necrotic.getId(), ModifierIds.sweeping.getId(),
      ModifierIds.fiery.getId(), ModifierIds.freezing.getId());
    this.tag(DAMAGE_UPGRADES).add(
      ModifierIds.sharpness.getId(), ModifierIds.pierce.getId(), ModifierIds.swiftstrike.getId(),
      ModifierIds.antiaquatic.getId(), ModifierIds.baneOfSssss.getId(), ModifierIds.cooling.getId(), ModifierIds.killager.getId(), ModifierIds.smite.getId());

    this.tag(HARVEST_UPGRADES).add(ModifierIds.haste.getId(), ModifierIds.blasting.getId(), ModifierIds.hydraulic.getId(), ModifierIds.lightspeed.getId());

    this.tag(GENERAL_ARMOR_UPGRADES).add(
      ModifierIds.fiery.getId(), ModifierIds.freezing.getId(), ModifierIds.thorns.getId(),
      ModifierIds.ricochet.getId(), ModifierIds.springy.getId(), ModifierIds.blockade.getId());
    this.tag(HELMET_UPGRADES).add(TinkerModifiers.itemFrame.getId(), ModifierIds.respiration.getId(), ModifierIds.minimap.getId()).addOptional(ModifierIds.headlight.getId());
    this.tag(CHESTPLATE_UPGRADES).add(ModifierIds.haste.getId(), ModifierIds.knockback.getId(), TinkerModifiers.sleeves.getId());
    this.tag(LEGGING_UPGRADES).add(ModifierIds.leaping.getId(), TinkerModifiers.shieldStrap.getId(), ModifierIds.speedy.getId(), ModifierIds.swiftSneak.getId(), ModifierIds.stepUp.getId());
    this.tag(BOOT_UPGRADES).add(ModifierIds.depthStrider.getId(), ModifierIds.featherFall.getId(), ModifierIds.longFall.getId(), ModifierIds.lightspeed.getId(), ModifierIds.soulspeed.getId());

    this.tag(RANGED_UPGRADES).add(
      ModifierIds.pierce.getId(), ModifierIds.power.getId(), ModifierIds.punch.getId(), ModifierIds.quickCharge.getId(),
      TinkerModifiers.sinistral.getId(), ModifierIds.trueshot.getId(),
      ModifierIds.fiery.getId(), ModifierIds.freezing.getId(),
      ModifierIds.arrowPierce.getId(), ModifierIds.bounce.getId(), ModifierIds.necrotic.getId(),
      ModifierIds.lure.getId(), ModifierIds.collecting.getId(), ModifierIds.fins.getId());

    // abilities
    this.tag(GENERAL_ABILITIES).add(
      ModifierIds.expanded.getId(), ModifierIds.gilded.getId(), ModifierIds.unbreakable.getId(),
      ModifierIds.luck.getId(), TinkerModifiers.melting.getId());
    this.tag(MELEE_ABILITIES).add(
      ModifierIds.blocking.getId(), TinkerModifiers.parrying.getId(),
      TinkerModifiers.dualWielding.getId(), ModifierIds.spilling.getId());
    this.tag(HARVEST_ABILITIES).add(ModifierIds.autosmelt.getId(), TinkerModifiers.exchanging.getId(), ModifierIds.silky.getId());
    this.tag(RANGED_ABILITIES).add(
      ModifierIds.bulkQuiver.getId(), ModifierIds.trickQuiver.getId(),
      ModifierIds.crystalshot.getId(), ModifierIds.multishot.getId(), ModifierIds.ballista.getId(),
      ModifierIds.grapple.getId(),
      ModifierIds.channeling.getId(), ModifierIds.returning.getId(),
      ModifierIds.slimeball.getId(), ModifierIds.sliver.getId());
    this.tag(INTERACTION_ABILITIES).add(
      ModifierIds.bucketing.getId(), ModifierIds.firestarter.getId(), ModifierIds.glowing.getId(),
      ModifierIds.pathing.getId(), ModifierIds.stripping.getId(), ModifierIds.tilling.getId(), ModifierIds.brushing.getId(),
      ModifierIds.spitting.getId(), ModifierIds.splashing.getId(), ModifierIds.slurping.getId(),
      ModifierIds.bonking.getId(), ModifierIds.flinging.getId(), ModifierIds.springing.getId(), ModifierIds.warping.getId(),
      ModifierIds.throwing.getId(), ModifierIds.drillAttack.getId());
    // armor
    this.tag(GENERAL_ARMOR_ABILITIES).add(ModifierIds.protection.getId(), TinkerModifiers.bursting.getId(), TinkerModifiers.wetting.getId());
    this.tag(HELMET_ABILITIES).add(ModifierIds.aquaAffinity.getId(), ModifierIds.slurping.getId());
    this.tag(CHESTPLATE_ABILITIES).add(TinkerModifiers.ambidextrous.getId(), ModifierIds.reach.getId(), ModifierIds.strength.getId(), ModifierIds.wings.getId());
    this.tag(LEGGING_ABILITIES).add(ModifierIds.pockets.getId(), ModifierIds.soulBelt.getId(), ModifierIds.toolBelt.getId(), ModifierIds.craftingTable.getId());
    this.tag(BOOT_ABILITIES).add(
      ModifierIds.bouncy.getId(), ModifierIds.doubleJump.getId(),
      ModifierIds.flamewake.getId(), ModifierIds.snowdrift.getId(), ModifierIds.tilling.getId(), ModifierIds.pathing.getId(), ModifierIds.frostWalker.getId(), ModifierIds.glowing.getId());
    this.tag(SHIELD_ABILITIES).add(ModifierIds.boundless.getId(), ModifierIds.reflecting.getId());

    // defense
    this.tag(PROTECTION_DEFENSE).add(
      ModifierIds.blastProtection.getId(), ModifierIds.fireProtection.getId(), ModifierIds.magicProtection.getId(),
      ModifierIds.meleeProtection.getId(), ModifierIds.projectileProtection.getId(),
      ModifierIds.dragonborn.getId(), ModifierIds.shulking.getId(), ModifierIds.turtleShell.getId());
    this.tag(SPECIAL_DEFENSE).add(ModifierIds.knockbackResistance.getId(), ModifierIds.revitalizing.getId());

    // slotless
    this.tag(GENERAL_SLOTLESS).add(
      TinkerModifiers.overslime.getId(), ModifierIds.worldbound.getId(),
      ModifierIds.offhanded.getId(), ModifierIds.blunted.getId(), ModifierIds.workbench.getId(),
      ModifierIds.blindshot.getId(), ModifierIds.barebow.getId());
    this.tag(BONUS_SLOTLESS).add(
      ModifierIds.draconic.getId(), ModifierIds.rebalanced.getId(), ModifierIds.redirected.getId(), TinkerModifiers.trim.getId(),
      ModifierIds.harmonious.getId(), ModifierIds.recapitated.getId(), ModifierIds.forecast.getId(), ModifierIds.writable.getId())
      .addOptional(ModifierIds.embossed.getId());
    this.tag(COSMETIC_SLOTLESS).add(
      ModifierIds.shiny.getId(),
      TinkerModifiers.dyed.getId(), TinkerModifiers.embellishment.getId(), TinkerModifiers.banner.getId(),
      ModifierIds.farsighted.getId(), ModifierIds.nearsighted.getId());

    // JEI
    this.tag(CRAFTING).add(ModifierIds.workbench.getId(), ModifierIds.craftingTable.getId());
    this.tag(SMELTING).add(ModifierIds.smelting.getId(), ModifierIds.autosmelt.getId());
    this.tag(MELTING).add(TinkerModifiers.melting.getId());
    this.tag(SEVERING).add(TinkerModifiers.severing.getId());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Tag Provider";
  }
}
