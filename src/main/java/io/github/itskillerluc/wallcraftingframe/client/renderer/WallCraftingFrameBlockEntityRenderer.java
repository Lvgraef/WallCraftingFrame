package io.github.itskillerluc.wallcraftingframe.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.itskillerluc.wallcraftingframe.block.WallCraftingFrameBlock;
import io.github.itskillerluc.wallcraftingframe.blockentity.WallCraftingFrameBlockEntity;
import io.github.itskillerluc.wallcraftingframe.init.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WallCraftingFrameBlockEntityRenderer implements BlockEntityRenderer<WallCraftingFrameBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public WallCraftingFrameBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(WallCraftingFrameBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.result != null && Minecraft.getInstance().level.getBlockState(pBlockEntity.getBlockPos()).getBlock().equals(BlockRegistry.WALL_CRAFTING_FRAME.get())) {
            switch (Minecraft.getInstance().level.getBlockState(pBlockEntity.getBlockPos()).getValue(WallCraftingFrameBlock.FACING)) {
                case NORTH -> {
                    pPoseStack.translate(0.5, 0.5, 0.95);
                }
                case SOUTH -> {
                    pPoseStack.translate(0.5, 0.5, 0.05);
                    pPoseStack.mulPose(new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), 180));

                }
                case WEST -> {
                    pPoseStack.translate(0.95, 0.5, 0.5);
                    pPoseStack.mulPose(new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), 90));
                }
                case EAST -> {
                    pPoseStack.translate(0.05, 0.5, 0.5);
                    pPoseStack.mulPose(new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), 270));
                }
            }
            pPoseStack.scale(0.4f, 0.4f, 0.4f);

            context.getItemRenderer().renderStatic(pBlockEntity.result, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, pBlockEntity.getLevel(), ((int) pBlockEntity.getBlockPos().asLong()));
        }
    }
}
