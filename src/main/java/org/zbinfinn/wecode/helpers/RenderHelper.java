package org.zbinfinn.wecode.helpers;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.zbinfinn.wecode.WeCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.client.render.RenderPhase.ENABLE_LIGHTMAP;
import static net.minecraft.client.render.RenderPhase.MIPMAP_BLOCK_ATLAS_TEXTURE;

public class RenderHelper {
    public static Vector4fc colorModulator = null;
    public static BlendFunction blendFunction = null;

    // Translucent render layer copy from 1.21.3
    private static final RenderLayer TRANSLUCENT = RenderLayer.of("translucent", 786432, true, true, RenderPipelines.TRANSLUCENT, RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true));

    public static class BlockRender {
        BlockState block;
        BlockPos pos;
        float alpha;
        float red, green, blue;
        boolean isEntityBlock;
        Text[] signText;
        public BlockRender(BlockState block, BlockPos pos, float alpha, float red, float green, float blue, boolean isEntityBlock, @Nullable Text[] signText) {
            this.block = block;
            this.pos = pos;
            this.alpha = alpha;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.isEntityBlock = isEntityBlock;
            this.signText = signText;
        }

        public void setRed (float red) {
            this.red = red;
        }
        public void setGreen (float green) {
            this.green = green;
        }
        public void setBlue (float blue) {
            this.blue = blue;
        }
        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }
    }

    private static final Set<BlockRender> renders = new HashSet<>();

    public static void queueBlockRender(BlockRender render) {
        renders.add(render);
    }

    public static void queueBlockRender(BlockState blockState, BlockPos blockPos, float alpha, float red, float green, float blue, @Nullable Text[] signText) {
        boolean isEntityBlock = blockState.getBlock() instanceof BlockEntityProvider;
        renders.add(new BlockRender(blockState, blockPos, alpha, red, green, blue, isEntityBlock, signText));
    }

    public static void queueBlockRender(BlockState blockState, BlockPos blockPos, float alpha, @Nullable Text[] signText) {
        queueBlockRender(blockState, blockPos, alpha, 1.0f, 1.0f, 1.0f, signText);
    }

    public static void queueBlockRender(BlockState blockState, BlockPos blockPos, @Nullable Text[] signText) {
        queueBlockRender(blockState, blockPos, 1f, signText);
    }

    private static final Random random = Random.create();
    public static void worldRenderLast(WorldRenderContext event) {
        MinecraftClient client = WeCode.MC;
        if (client.world == null || client.player == null) {
            return;
        }

        List<BlockRender> solidBlocks = new ArrayList<>();
        List<BlockRender> translucentBlocks = new ArrayList<>();

        for (BlockRender render : renders) {
            if (render.alpha != 1) translucentBlocks.add(render);
            else solidBlocks.add(render);
        }

        // Sort translucent blocks back to front so we can see them through each other correctly
        Vec3d cameraPos = event.camera().getCameraPos();
        translucentBlocks.sort((a, b) -> {
            Box boxA = new Box(a.pos);
            Box boxB = new Box(b.pos);

            Vec3d closestA = new Vec3d(
                    Math.clamp(cameraPos.x, boxA.minX, boxA.maxX),
                    Math.clamp(cameraPos.y, boxA.minY, boxA.maxY),
                    Math.clamp(cameraPos.z, boxA.minZ, boxA.maxZ)
            );
            Vec3d closestB = new Vec3d(
                    Math.clamp(cameraPos.x, boxB.minX, boxB.maxX),
                    Math.clamp(cameraPos.y, boxB.minY, boxB.maxY),
                    Math.clamp(cameraPos.z, boxB.minZ, boxB.maxZ)
            );

            return Double.compare(closestB.squaredDistanceTo(cameraPos), closestA.squaredDistanceTo(cameraPos));
        });

        VertexConsumerProvider.Immediate provider = client.getBufferBuilders().getEntityVertexConsumers();
        renderList(solidBlocks, event, provider);
        renderList(translucentBlocks, event, provider);
        provider.draw();

        colorModulator = null;
        blendFunction = null;
        renders.clear();
    }

    private static void renderList(List<BlockRender> blockRenders, WorldRenderContext event, VertexConsumerProvider.Immediate provider) {
        MatrixStack matrices = event.matrixStack();
        Camera camera = event.camera();

        MinecraftClient client = WeCode.MC;
        BlockRenderManager blockRenderManager = client.getBlockRenderManager();

        for (BlockRender render : blockRenders) {
            matrices.push();
            matrices.translate(render.pos.getX() - camera.getPos().x, render.pos.getY() - camera.getPos().y, render.pos.getZ() - camera.getPos().z);

            // Apply translucency for blocks, set blendFunction for translucency with block entities
            colorModulator = new Vector4f(render.red, render.green, render.blue, render.alpha);
            if (render.alpha != 1) blendFunction = BlendFunction.TRANSLUCENT;
            else blendFunction = null;

            random.setSeed(render.block.getRenderingSeed(render.pos));
            BlockStateModel blockModel = blockRenderManager.getModel(render.block);
            List<BlockModelPart> modelParts = blockModel.getParts(random);
            VertexConsumer vertexConsumer = provider.getBuffer(TRANSLUCENT);

            blockRenderManager.renderBlock(
                    render.block,
                    render.pos,
                    event.world(),
                    matrices,
                    vertexConsumer,
                    true,
                    modelParts
            );

            // Check if the block is a BlockEntityProvider (like chests)
            if (render.isEntityBlock) {
                // Correctly get the block entity (e.g., chest)
                BlockEntity blockEntity = createTemporaryBlockEntity(render);

                if (blockEntity != null) {
                    // Get the light level at the pos so the block entity doesn't look out of place when it's dark/nighttime
                    LightingProvider lighting = event.world().getLightingProvider();
                    int worldLight = LightmapTextureManager.pack(
                            lighting.get(LightType.BLOCK).getLightLevel(render.pos),
                            lighting.get(LightType.SKY).getLightLevel(render.pos)
                    );

                    // Render the block entity (e.g., chest) with appropriate transparency
                    client.getBlockEntityRenderDispatcher().get(blockEntity).render(
                            blockEntity, // the BlockEntity
                            camera.getLastTickProgress(),
                            matrices, // MatrixStack
                            provider, // VertexConsumerProvider
                            worldLight, // Light Level
                            OverlayTexture.DEFAULT_UV, // Overlay
                            camera.getCameraPos()
                    );
                }
            }

            matrices.pop();

            // Draw right away so translucency will work correctly when sodium is also active
            provider.draw();
        }
    }

    private static BlockEntity createTemporaryBlockEntity(BlockRender render) {
        // Replace with the block entity type you want to render (e.g., SignBlockEntity for signs)
        if (render.block.getBlock() instanceof WallSignBlock) {
            return createTemporarySignBlockEntity(render);
        } else if (render.block.getBlock() instanceof ChestBlock) {
            return new ChestBlockEntity(render.pos, render.block);
        }

        // Return null if no temporary BlockEntity is needed
        return null;
    }

    private static SignBlockEntity createTemporarySignBlockEntity(BlockRender render) {
        // Create the SignBlockEntity for rendering
        SignBlockEntity signEntity = new SignBlockEntity(render.pos, render.block);
        signEntity.setWorld(WeCode.MC.world);

        // Set the text for the sign (4 lines of text, but you can adjust this as needed)
        signEntity.setText(new SignText(
                render.signText,
                render.signText,
                DyeColor.BLACK,
                false
        ), true);

        signEntity.setCachedState(render.block);

        return signEntity;
    }
}
