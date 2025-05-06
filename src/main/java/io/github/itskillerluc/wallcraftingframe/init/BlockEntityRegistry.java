package io.github.itskillerluc.wallcraftingframe.init;

import io.github.itskillerluc.wallcraftingframe.WallCraftingFrame;
import io.github.itskillerluc.wallcraftingframe.blockentity.WallCraftingFrameBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WallCraftingFrame.MODID);

    public static final RegistryObject<BlockEntityType<WallCraftingFrameBlockEntity>> WALL_CRAFTING_FRAME = BLOCK_ENTITY_REGISTRY.register("wall_crafting_frame",
            () -> BlockEntityType.Builder.of(WallCraftingFrameBlockEntity::new, BlockRegistry.WALL_CRAFTING_FRAME.get()).build(null));

}
