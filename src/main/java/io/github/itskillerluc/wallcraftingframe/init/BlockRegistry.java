package io.github.itskillerluc.wallcraftingframe.init;

import io.github.itskillerluc.wallcraftingframe.WallCraftingFrame;
import io.github.itskillerluc.wallcraftingframe.block.WallCraftingFrameBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, WallCraftingFrame.MODID);

    public static final RegistryObject<WallCraftingFrameBlock> WALL_CRAFTING_FRAME = BLOCK_REGISTRY.register("wall_crafting_frame",
            () -> new WallCraftingFrameBlock(BlockBehaviour.Properties.of().instabreak().noCollission().noOcclusion()));
}
