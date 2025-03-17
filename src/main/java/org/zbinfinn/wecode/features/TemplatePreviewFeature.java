package org.zbinfinn.wecode.features;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.helpers.RenderHelper;
import org.zbinfinn.wecode.templates.CodeBlock;
import org.zbinfinn.wecode.templates.Template;
import org.zbinfinn.wecode.util.Constants;
import org.zbinfinn.wecode.util.TemplateUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TemplatePreviewFeature extends Feature {

    private float alphaPercentage = 0;
    private float direction = 0.01f;
    private Set<RenderHelper.BlockRender> renders = new HashSet<>();
    private boolean validPlacement = true;

    private final Set<String> noChest = new HashSet<>();
    private final Set<String> noStone = new HashSet<>();
    private final Set<String> withSpace = new HashSet<>();
    private final Set<String> noSign = new HashSet<>();
    private final HashMap<String, BlockState> blocks = new HashMap<>();

    private ItemStack currentItem = ItemStack.EMPTY;
    private Template currentTemplate;

    private final KeyBinding pinKeyBinding = new KeyBinding(
            "key.wecode.pintemplatepreview",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_P,
            "key.wecode.category"
    );
    boolean pinned = false;

    private BlockPos location;

    @Override
    public void activate() {
        KeyBindingHelper.registerKeyBinding(pinKeyBinding);

        blocks.put("event", Blocks.DIAMOND_BLOCK.getDefaultState());
        blocks.put("player_action", Blocks.COBBLESTONE.getDefaultState());
        blocks.put("if_player", Blocks.OAK_PLANKS.getDefaultState());
        blocks.put("open_norm", Blocks.PISTON.getDefaultState().with(Properties.FACING, Direction.SOUTH));
        blocks.put("close_norm", Blocks.PISTON.getDefaultState().with(Properties.FACING, Direction.NORTH));
        blocks.put("open_repeat", Blocks.STICKY_PISTON.getDefaultState().with(Properties.FACING, Direction.SOUTH));
        blocks.put("close_repeat", Blocks.STICKY_PISTON.getDefaultState().with(Properties.FACING, Direction.NORTH));
        blocks.put("repeat", Blocks.PRISMARINE.getDefaultState());
        blocks.put("entity_action", Blocks.MOSSY_COBBLESTONE.getDefaultState());
        blocks.put("if_entity", Blocks.BRICKS.getDefaultState());
        blocks.put("set_var", Blocks.IRON_BLOCK.getDefaultState());
        blocks.put("if_var", Blocks.OBSIDIAN.getDefaultState());
        blocks.put("game_action", Blocks.NETHERRACK.getDefaultState());
        blocks.put("if_game", Blocks.RED_NETHER_BRICKS.getDefaultState());
        blocks.put("select_obj", Blocks.PURPUR_BLOCK.getDefaultState());
        blocks.put("else", Blocks.END_STONE.getDefaultState());
        blocks.put("call_func", Blocks.LAPIS_ORE.getDefaultState());
        blocks.put("start_process", Blocks.EMERALD_ORE.getDefaultState());
        blocks.put("control", Blocks.COAL_BLOCK.getDefaultState());
        blocks.put("entity_event", Blocks.GOLD_BLOCK.getDefaultState());
        blocks.put("func", Blocks.LAPIS_BLOCK.getDefaultState());
        blocks.put("process", Blocks.EMERALD_BLOCK.getDefaultState());

        noChest.add("open_norm");
        noChest.add("close_norm");
        noChest.add("event");
        noChest.add("open_repeat");
        noChest.add("close_repeat");
        noChest.add("else");
        noChest.add("entity_event");

        noStone.add("open_norm");
        noStone.add("close_norm");
        noStone.add("if_player");
        noStone.add("if_entity");
        noStone.add("if_var");
        noStone.add("if_game");
        noStone.add("open_repeat");
        noStone.add("close_repeat");
        noStone.add("repeat");
        noStone.add("else");

        noSign.add("open_repeat");
        noSign.add("close_repeat");
        noSign.add("open_norm");
        noSign.add("close_norm");
        noSign.add("else");

        withSpace.add("close_norm");
        withSpace.add("close_repeat");
    }

    @Override
    public void tick() {
        if (pinKeyBinding.wasPressed()) {
            double DURATION = 3;
            pinned = !pinned;
            if (pinned) {
                NotificationHelper.sendNotificationWithSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.5f, 2f, Text.literal("Pinned"), NotificationHelper.NotificationType.MOD_NORMAL, DURATION);
            } else {
                NotificationHelper.sendNotificationWithSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.5f, 2f, Text.literal("Unpinned"), NotificationHelper.NotificationType.MOD_NORMAL, DURATION);
            }
        }
    }

    @Override
    public void worldRenderLast(WorldRenderContext event) {
        MinecraftClient client = WeCode.MC;

        if (client.world == null || client.player == null) {
            return;
        }

        if (!pinned){
            if (!currentItem.equals(client.player.getMainHandStack())) {
                currentItem = client.player.getMainHandStack();
                try {
                    currentTemplate = TemplateUtil.fromItem(currentItem);
                } catch (Exception ignored) {
                    currentTemplate = null;
                }
            }
        }

        if (currentTemplate == null) {
            return;
        }

        ClientPlayerEntity player = client.player;

        alphaPercentage += direction;
        if (alphaPercentage > 1.0f || alphaPercentage < 0.0f) {
            direction = -direction;
        }

        double maxDistance = 20.0D;


        if (!pinned) {
            HitResult hitResult = player.raycast(maxDistance, 1, false);
            if (hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            location = blockHit.getBlockPos().add(0, 1, 0);
        }

        if (location == null) {
            return;
        }

        if (!Constants.CODESPACE_BLOCKS.contains(client.world.getBlockState(location.add(0, -1, 0)).getBlock())) {
            return;
        }

        BlockPos currentPos = location.mutableCopy().toImmutable();

        validPlacement = true;

        for (CodeBlock block : currentTemplate.getCodeBlocks()) {
            if (block.getBlock() == null) {
                continue;
            }
            checkCurrentPos(currentPos);

            if (withSpace.contains(block.getBlock())) {
                currentPos = currentPos.add(0, 0, 1);
            }

            renderNormal(blocks.getOrDefault(block.getBlock(), Blocks.RED_CONCRETE.getDefaultState()), currentPos);

            if (!noChest.contains(block.getBlock())) {
                currentPos = currentPos.add(0, 1, 0);
                checkCurrentPos(currentPos);
                renderChest(currentPos);
                currentPos = currentPos.add(0, -1, 0);
            }

            if (!noSign.contains(block.getBlock())) {
                currentPos = currentPos.add(-1, 0, 0);
                checkCurrentPos(currentPos);
                renderSign(currentPos, block);
                currentPos = currentPos.add(1, 0, 0);
            }

            if (!noStone.contains(block.getBlock())) {
                currentPos = currentPos.add(0, 0, 1);
                checkCurrentPos(currentPos);
                renderNormal(Blocks.STONE.getDefaultState(), currentPos);
            }

            checkCurrentPos(currentPos);

            currentPos = currentPos.add(0, 0, 1);
        }

        for (RenderHelper.BlockRender render : renders) {
            render.setBlue(getBlue());
            render.setGreen(getGreen());
            render.setRed(getRed());
            RenderHelper.queueBlockRender(render);
        }

        renders.clear();
    }

    private void checkCurrentPos(BlockPos currentPos) {
        if (!WeCode.MC.world.getBlockState(currentPos).getBlock().equals(Blocks.AIR)) {
            validPlacement = false;
        }
    }

    private void renderSign(BlockPos currentPos, CodeBlock block) {
        renders.add(new RenderHelper.BlockRender((Blocks.OAK_WALL_SIGN.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.WEST)), currentPos, getAlpha(), 1, 1, 1, true,
                new Text[]{
                        //Text.literal(block.getBlock().toUpperCase()),
                        //Text.literal(block.getAction()),
                        Text.literal(block.getSignLineOne()),
                        Text.literal(block.getSignLineTwo()),
                        Text.literal(block.getSignLineThree()),
                        Text.literal(block.getSignLineFour()),
                }));
    }

    private void renderNormal(BlockState block, BlockPos pos) {
        renders.add(new RenderHelper.BlockRender(block, pos, getAlpha(), 1, 1 , 1, false, null));
    }

    private void renderChest(BlockPos pos) {
        BlockState chestState = Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.EAST);
        renders.add(new RenderHelper.BlockRender(chestState, pos, getAlpha(), 1, 1, 1, true, null));
    }

    @Override
    public boolean isEnabled() {
        return Config.getConfig().TemplatePeeker;
    }

    private float getAlpha() {
        return 1f;
    }

    private float getBlue() {
        return validPlacement ? 1.5f : .8f;
    }

    private float getRed() {
        return validPlacement ? .8f : 1.5f;
    }

    private float getGreen() {
        return .8f;
    }
}

