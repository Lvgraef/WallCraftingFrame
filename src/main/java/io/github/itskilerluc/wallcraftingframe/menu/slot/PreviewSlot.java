package io.github.itskilerluc.wallcraftingframe.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PreviewSlot extends ResultSlot {
    public PreviewSlot(Player pPlayer, CraftingContainer pCraftSlots, Container pContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pPlayer, pCraftSlots, pContainer, pSlot, pXPosition, pYPosition);
    }

    @Override
    public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
