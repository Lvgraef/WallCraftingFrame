package io.github.itskillerluc.wallcraftingframe.menu.util.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreviewSlot extends ResultSlot {
    public PreviewSlot(Player pPlayer, CraftingContainer pCraftSlots, Container pContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pPlayer, pCraftSlots, pContainer, pSlot, pXPosition, pYPosition);
    }

    @Override
    public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
    }

    @Override
    public Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
        return Optional.of(ItemStack.EMPTY);
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
