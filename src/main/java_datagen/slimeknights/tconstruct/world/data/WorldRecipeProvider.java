package slimeknights.tconstruct.world.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class WorldRecipeProvider extends BaseRecipeProvider  {
  public WorldRecipeProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  protected void buildRecipes(RecipeOutput consumer) {
    // Add recipe for all slimeball <-> congealed and slimeblock <-> slimeball
    // only earth slime recipe we need here slime
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                       .define('#', SlimeType.EARTH.getSlimeballTag())
                       .pattern("##")
                       .pattern("##")
                       .unlockedBy("has_item", has(SlimeType.EARTH.getSlimeballTag()))
                       .group("tconstruct:congealed_slime")
                       .save(consumer, modResource("common/slime/earth/congealed"));

    // does not need green as its the fallback
    for (SlimeType slimeType : SlimeType.TINKER) {
      ResourceLocation name = modResource("common/slime/" + slimeType.getSerializedName() + "/congealed");
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerWorld.congealedSlime.get(slimeType))
                         .define('#', slimeType.getSlimeballTag())
                         .pattern("##")
                         .pattern("##")
                         .unlockedBy("has_item", has(slimeType.getSlimeballTag()))
                         .group("tconstruct:congealed_slime")
                         .save(consumer, name);
      ResourceLocation blockName = modResource("common/slime/" + slimeType.getSerializedName() + "/slimeblock");
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerWorld.slime.get(slimeType))
                         .define('#', slimeType.getSlimeballTag())
                         .pattern("###")
                         .pattern("###")
                         .pattern("###")
                         .unlockedBy("has_item", has(slimeType.getSlimeballTag()))
                         .group("slime_blocks")
                         .save(consumer, blockName);
      // green already can craft into slime balls
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerCommons.slimeball.get(slimeType), 9)
                            .requires(TinkerWorld.slime.get(slimeType))
                            .unlockedBy("has_item", has(TinkerWorld.slime.get(slimeType)))
                            .group("tconstruct:slime_balls")
                            .save(consumer, "tconstruct:common/slime/" + slimeType.getSerializedName() + "/slimeball_from_block");
    }
    // all types of congealed need a recipe to a block
    for (SlimeType slimeType : SlimeType.values()) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerCommons.slimeball.get(slimeType), 4)
                            .requires(TinkerWorld.congealedSlime.get(slimeType))
                            .unlockedBy("has_item", has(TinkerWorld.congealedSlime.get(slimeType)))
                            .group("tconstruct:slime_balls")
                            .save(consumer, "tconstruct:common/slime/" + slimeType.getSerializedName() + "/slimeball_from_congealed");
    }

    // craft other slime based items, forge does not automatically add recipes using the tag anymore
    RecipeOutput slimeConsumer = withCondition(consumer, ConfigEnabledCondition.SLIME_RECIPE_FIX);
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON)
                       .pattern("#")
                       .pattern("P")
                       .define('#', Tags.Items.SLIMEBALLS)
                       .define('P', Blocks.PISTON)
                       .unlockedBy("has_slime_ball", has(Tags.Items.SLIMEBALLS))
                       .save(slimeConsumer, modResource("common/slime/sticky_piston"));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.LEAD, 2)
                       .define('~', Items.STRING)
                       .define('O', Tags.Items.SLIMEBALLS)
                       .pattern("~~ ")
                       .pattern("~O ")
                       .pattern("  ~")
                       .unlockedBy("has_slime_ball", has(Tags.Items.SLIMEBALLS))
                       .save(slimeConsumer, modResource("common/slime/lead"));

    // wood
    String woodFolder = "world/wood/";
    woodCrafting(consumer, TinkerWorld.greenheart, woodFolder + "greenheart/");
    woodCrafting(consumer, TinkerWorld.skyroot, woodFolder + "skyroot/");
    woodCrafting(consumer, TinkerWorld.bloodshroom, woodFolder + "bloodshroom/");
    woodCrafting(consumer, TinkerWorld.enderbark, woodFolder + "enderbark/");

    // geodes
    geodeRecipes(consumer, TinkerWorld.earthGeode, SlimeType.EARTH, "common/slime/earth/");
    geodeRecipes(consumer, TinkerWorld.skyGeode,   SlimeType.SKY,   "common/slime/sky/");
    geodeRecipes(consumer, TinkerWorld.ichorGeode, SlimeType.ICHOR, "common/slime/ichor/");
    geodeRecipes(consumer, TinkerWorld.enderGeode, SlimeType.ENDER, "common/slime/ender/");
  }

  private void geodeRecipes(RecipeOutput consumer, GeodeItemObject geode, SlimeType slime, String folder) {
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, geode.getBlock())
                       .define('#', geode.asItem())
                       .pattern("##")
                       .pattern("##")
                       .unlockedBy("has_item", has(geode.asItem()))
                       .group("tconstruct:slime_crystal_block")
                       .save(consumer, modResource(folder + "crystal_block"));
    SimpleCookingRecipeBuilder.blasting(Ingredient.of(geode), RecipeCategory.MISC, TinkerCommons.slimeball.get(slime), 0.2f, 200)
                              .unlockedBy("has_crystal", has(geode))
                              .group("tconstruct:slime_crystal")
                              .save(consumer, modResource(folder + "crystal_smelting"));
    ItemLike dirt = TinkerWorld.slimeDirt.get(slime.asDirt());
    SimpleCookingRecipeBuilder.blasting(Ingredient.of(dirt), RecipeCategory.MISC, geode, 0.2f, 400)
                              .unlockedBy("has_dirt", has(dirt))
                              .group("tconstruct:slime_dirt")
                              .save(consumer, modResource(folder + "crystal_growing"));
  }

  protected void woodCrafting(net.minecraft.data.recipes.RecipeOutput consumer, slimeknights.mantle.registration.object.WoodBlockObject wood, String folder) {
    net.minecraft.advancements.Criterion<net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance> hasPlanks = has(wood);

    // planks
    net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, wood, 4).requires(wood.getLogItemTag())
                          .group("planks")
                          .unlockedBy("has_log", has(wood.getLogItemTag()))
                          .save(consumer, modResource(folder + "planks"));
    // slab
    net.minecraft.world.level.ItemLike slab = wood.getSlab();
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, slab, 6)
                       .define('#', wood)
                       .pattern("###")
                       .unlockedBy("has_planks", hasPlanks)
                       .group("wooden_slab")
                       .save(consumer, modResource(folder + "slab"));
    // stairs
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, wood.getStairs(), 4)
                       .define('#', wood)
                       .pattern("#  ")
                       .pattern("## ")
                       .pattern("###")
                       .unlockedBy("has_planks", hasPlanks)
                       .group("wooden_stairs")
                       .save(consumer, modResource(folder + "stairs"));

    // log to wood
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, wood.getWood(), 3)
                       .define('#', wood.getLog())
                       .pattern("##").pattern("##")
                       .group("bark")
                       .unlockedBy("has_log", has(wood.getLog()))
                       .save(consumer, modResource(folder + "log_to_wood"));
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, wood.getStrippedWood(), 3)
                       .define('#', wood.getStrippedLog())
                       .pattern("##").pattern("##")
                       .group("bark")
                       .unlockedBy("has_log", has(wood.getStrippedLog()))
                       .save(consumer, modResource(folder + "stripped_log_to_wood"));
    // doors
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.DECORATIONS, wood.getFence(), 3)
                       .define('#', net.neoforged.neoforge.common.Tags.Items.RODS_WOODEN).define('W', wood)
                       .pattern("W#W").pattern("W#W")
                       .group("wooden_fence")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "fence"));
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, wood.getFenceGate())
                       .define('#', net.minecraft.world.item.Items.STICK).define('W', wood)
                       .pattern("#W#").pattern("#W#")
                       .group("wooden_fence_gate")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "fence_gate"));
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, wood.getDoor(), 3)
                       .define('#', wood)
                       .pattern("##").pattern("##").pattern("##")
                       .group("wooden_door")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "door"));
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, wood.getTrapdoor(), 2)
                       .define('#', wood)
                       .pattern("###").pattern("###")
                       .group("wooden_trapdoor")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "trapdoor"));
    // buttons
    net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless(net.minecraft.data.recipes.RecipeCategory.REDSTONE, wood.getButton())
                          .requires(wood)
                          .group("wooden_button")
                          .unlockedBy("has_planks", hasPlanks)
                          .save(consumer, modResource(folder + "button"));
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, wood.getPressurePlate())
                       .define('#', wood)
                       .pattern("##")
                       .group("wooden_pressure_plate")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "pressure_plate"));
    // signs
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.DECORATIONS, wood.getSign(), 3)
                       .group("sign")
                       .define('#', wood).define('X', net.neoforged.neoforge.common.Tags.Items.RODS_WOODEN)
                       .pattern("###").pattern("###").pattern(" X ")
                       .unlockedBy("has_planks", has(wood))
                       .save(consumer, modResource(folder + "sign"));
    net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.DECORATIONS, wood.getHangingSign(), 6)
                       .group("hanging_sign")
                       .define('#', wood.getStrippedLog())
                       .define('X', net.minecraft.world.item.Items.CHAIN)
                       .pattern("X X").pattern("###").pattern("###")
                       .unlockedBy("has_stripped_logs", has(wood.getStrippedLog()))
                       .save(consumer, modResource(folder + "hanging_sign"));
  }

}
