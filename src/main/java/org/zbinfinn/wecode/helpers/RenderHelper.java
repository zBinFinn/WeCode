package org.zbinfinn.wecode.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.zbinfinn.wecode.WeCode;

import java.util.HashSet;
import java.util.Set;

public class RenderHelper {
    static class BlockRender {
        BlockState block;
        BlockPos pos;
        float alpha;
        float red, green, blue;
        boolean isEntityBlock;
        public BlockRender(BlockState block, BlockPos pos, float alpha, float red, float green, float blue, boolean isEntityBlock) {
            this.block = block;
            this.pos = pos;
            this.alpha = alpha;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    private static final Set<BlockRender> renders = new HashSet<>();

    public static void queueBlockRender(BlockState blockState, BlockPos blockPos, float alpha, float red, float green, float blue) {
        boolean isEntityBlock = blockState.getBlock() instanceof BlockEntityProvider;
        renders.add(new BlockRender(blockState, blockPos, alpha, red, green, blue, isEntityBlock));
    }

    public static void queueBlockRender(BlockState blockState, BlockPos blockPos, float alpha) {
        queueBlockRender(blockState, blockPos, alpha, 1.0f, 1.0f, 1.0f);
    }

    public static void queueBlockRender(BlockState blockState, BlockPos blockPos) {
        queueBlockRender(blockState, blockPos, 1f);
    }

    public static void worldRenderLast(WorldRenderContext event) {
        MinecraftClient client = WeCode.MC;
        if (client.world == null || client.player == null) {
            return;
        }

        BlockRenderManager blockRenderManager = client.getBlockRenderManager();

        Camera camera = event.camera();
        MatrixStack matrices = event.matrixStack();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();

        for (BlockRender render : renders) {
            matrices.push();
            matrices.translate(render.pos.getX() - camera.getPos().x, render.pos.getY() - camera.getPos().y, render.pos.getZ() - camera.getPos().z);

            // Do that for invalid template placement RenderSystem.setShaderColor(1F, 0.7F, 0.7F, render.alpha);
            RenderSystem.setShaderColor(1F, 1F, 1F, render.alpha);

            var blockModel = blockRenderManager.getModel(render.block);

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

            blockRenderManager.getModelRenderer().render(
                    matrices.peek(),
                    vertexConsumer,
                    render.block,
                    blockModel,
                    render.red, render.green, render.blue,
                    15728880,
                    OverlayTexture.DEFAULT_UV
            );

            matrices.pop();
        }
        vertexConsumers.draw();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renders.clear();
    }

}
