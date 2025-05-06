package io.github.itskillerluc.wallcraftingframe.client.event;

import io.github.itskillerluc.wallcraftingframe.WallCraftingFrame;
import io.github.itskillerluc.wallcraftingframe.client.renderer.WallCraftingFrameBlockEntityRenderer;
import io.github.itskillerluc.wallcraftingframe.init.BlockEntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = WallCraftingFrame.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.WALL_CRAFTING_FRAME.get(), WallCraftingFrameBlockEntityRenderer::new);
    }
}
