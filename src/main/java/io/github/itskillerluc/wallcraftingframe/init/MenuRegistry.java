package io.github.itskillerluc.wallcraftingframe.init;

import io.github.itskillerluc.wallcraftingframe.WallCraftingFrame;
import io.github.itskillerluc.wallcraftingframe.menu.WallCraftingFrameMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, WallCraftingFrame.MODID);

    public static final RegistryObject<MenuType<WallCraftingFrameMenu>> WALL_CRAFTING_FRAME_MENU = MENU_REGISTRY.register("wall_crafting_frame_menu",
            () -> IForgeMenuType.create(WallCraftingFrameMenu::new));
}
