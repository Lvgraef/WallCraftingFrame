package io.github.itskillerluc.wallcraftingframe.item;

import io.github.itskillerluc.wallcraftingframe.init.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WallCraftingFrameItem extends BlockItem {
    public WallCraftingFrameItem(Properties pProperties) {
        super(BlockRegistry.WALL_CRAFTING_FRAME.get(), pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable("block.wallcraftingframe.wallcraftingframe.tooltip1").withStyle(ChatFormatting.GRAY));
        pTooltip.add(Component.translatable("block.wallcraftingframe.wallcraftingframe.tooltip2").withStyle(ChatFormatting.GRAY));
    }
}
