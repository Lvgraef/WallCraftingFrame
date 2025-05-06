package io.github.itskillerluc.wallcraftingframe.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Slot.class)
public interface SlotMixin {
    @Invoker("onSwapCraft")
    void invokeOnSwapCraft(int pNumItemsCrafted);
}
