package io.github.itskillerluc.wallcraftingframe.init;

import io.github.itskillerluc.wallcraftingframe.WallCraftingFrame;
import io.github.itskillerluc.wallcraftingframe.item.WallCraftingFrameItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, WallCraftingFrame.MODID);

    public static final RegistryObject<WallCraftingFrameItem> WALL_CRAFTING_FRAME = ITEM_REGISTRY.register("wall_crafting_frame",
            () -> new WallCraftingFrameItem(new Item.Properties()));
}
