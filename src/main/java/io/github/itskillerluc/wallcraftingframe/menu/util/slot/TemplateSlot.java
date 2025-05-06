package io.github.itskillerluc.wallcraftingframe.menu.util.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TemplateSlot extends Slot {
    public TemplateSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public void onQuickCraft(@NotNull ItemStack pOldStack, @NotNull ItemStack pNewStack) {
    }

    public int getMaxStackSize() {
        return 64;
    }

    public int getMaxStackSize(@NotNull ItemStack pStack) {
        return 64;
    }

    public @NotNull ItemStack safeTake(int pCount, int pDecrement, @NotNull Player pPlayer) {
        this.set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
        this.set(ItemStack.EMPTY);
        return Optional.of(ItemStack.EMPTY);
    }

    public @NotNull ItemStack safeInsert(@NotNull ItemStack pStack) {
        return this.safeInsert(pStack, pStack.getCount());
    }

    public @NotNull ItemStack safeInsert(ItemStack pStack, int pIncrement) {
        this.set(new ItemStack(pStack.getItem(), 1));
        return pStack;
    }

    public boolean allowModification(@NotNull Player pPlayer) {
        return true;
    }
}
