package io.github.itskilerluc.wallcraftingframe;

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

    }
}
