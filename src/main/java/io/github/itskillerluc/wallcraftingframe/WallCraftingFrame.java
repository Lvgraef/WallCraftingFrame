package io.github.itskillerluc.wallcraftingframe;

import io.github.itskillerluc.wallcraftingframe.init.BlockEntityRegistry;
import io.github.itskillerluc.wallcraftingframe.init.BlockRegistry;
import io.github.itskillerluc.wallcraftingframe.init.ItemRegistry;
import io.github.itskillerluc.wallcraftingframe.init.MenuRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WallCraftingFrame.MODID)
public class WallCraftingFrame {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "wallcraftingframe";

    public WallCraftingFrame(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        BlockRegistry.BLOCK_REGISTRY.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_REGISTRY.register(modEventBus);
        ItemRegistry.ITEM_REGISTRY.register(modEventBus);
        MenuRegistry.MENU_REGISTRY.register(modEventBus);

        modEventBus.addListener(WallCraftingFrame::registerToCreativeTabs);
    }

    private static void registerToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ItemRegistry.WALL_CRAFTING_FRAME.get());
        }
    }
}
