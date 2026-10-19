package slimeknights.tconstruct.library.tools.part.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.MaterialItem;

import javax.annotation.Nullable;
import java.util.List;

/** Implementation of {@link MaterialItem} on a {@link BlockItem}. */
public class MaterialBlockItem extends BlockItem implements IMaterialItem {
  public MaterialBlockItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public MaterialVariantId getMaterial(ItemStack stack) {
    var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
    return MaterialItem.getMaterialId(customData != null ? customData.copyTag() : null);
  }

  @Override
  public Component getName(ItemStack stack) {
    return MaterialItem.getName(this, stack);
  }

  public Rarity getRarity(ItemStack stack) {
    return MaterialRegistry.getMaterial(getMaterial(stack).getMaterialId()).getRarity();
  }

  public boolean isFoil(ItemStack stack) {
    return MaterialRegistry.getInstance().isInTag(getMaterial(stack).getMaterialId(), TinkerTags.Materials.SHINY);
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    MaterialItem.appendHoverText(this, stack, context, tooltip, flag);
    super.appendHoverText(stack, context, tooltip, flag);
  }

  @Nullable
  public String getCreatorModId(ItemStack stack) {
    return MaterialItem.getCreatorModId(this, stack);
  }

  public void verifyTagAfterLoad(CompoundTag tag) {
    MaterialItem.verifyTag(tag);
  }
}
