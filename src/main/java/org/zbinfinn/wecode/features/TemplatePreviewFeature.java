package org.zbinfinn.wecode.features;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.zbinfinn.wecode.Config;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.RenderHelper;
import org.zbinfinn.wecode.templates.CodeBlock;
import org.zbinfinn.wecode.templates.Template;
import org.zbinfinn.wecode.util.NumberUtil;
import org.zbinfinn.wecode.util.TemplateUtil;

import java.util.HashMap;

public class TemplatePreviewFeature extends Feature {

    private float alphaPercentage = 0;
    private float direction = 0.01f;

    private final HashMap<String, Block> blocks = new HashMap<>();

    @Override
    public void activate() {
        blocks.put("event", Blocks.DIAMOND_BLOCK);
        blocks.put("player_action", Blocks.COBBLESTONE);
        blocks.put("if_player", Blocks.OAK_PLANKS);
    }

    @Override
    public void worldRenderLast(WorldRenderContext event) {
        MinecraftClient client = WeCode.MC;
        ClientPlayerEntity player = client.player;

        alphaPercentage += direction;
        if (alphaPercentage > 1.0f || alphaPercentage < 0.0f) {
            direction = -direction;
        }

        double maxDistance = 20.0D;

        HitResult hitResult = player.raycast(maxDistance, 1, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().add(0, 1, 0);
        // targetPos now holds the location of the block the player is looking at

        //RenderHelper.queueBlockRender(Blocks.COBBLESTONE.getDefaultState(), targetPos, (float) NumberUtil.hotLerp(0.3, 0.7, alphaPercentage));
        try {
            Template template = TemplateUtil.fromItem(client.player.getMainHandStack());

            BlockPos currentPos = targetPos;

            for (CodeBlock block : template.getCodeBlocks()) {
                if (block.getBlock() == null) {
                    continue;
                }
                renderNormal(blocks.getOrDefault(block.getBlock(), Blocks.RED_CONCRETE), currentPos);
                currentPos = currentPos.add(0, 1, 0);
                renderChest(currentPos);
                currentPos = currentPos.add(0, -1, 0);
                currentPos = currentPos.add(0, 0, 1);
                renderNormal(Blocks.STONE, currentPos);
                currentPos = currentPos.add(0, 0, 1);

            }

        } catch (Exception ignored) {

        }
    }

    private void renderNormal(Block block, BlockPos pos) {
        RenderHelper.queueBlockRender(block.getDefaultState(), pos, (float) NumberUtil.hotLerp(0.3, 0.7, alphaPercentage));
    }

    private void renderChest(BlockPos pos) {
        BlockState chestState = Blocks.CHEST.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH);

        RenderHelper.queueBlockRender(chestState, pos, (float) NumberUtil.hotLerp(0.3, 0.7, alphaPercentage));
    }


    @Override
    public boolean isEnabled() {
        return Config.getConfig().TemplatePeeker;
    }
}

