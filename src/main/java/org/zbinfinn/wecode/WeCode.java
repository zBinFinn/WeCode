package org.zbinfinn.wecode;

import com.google.gson.Gson;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zbinfinn.wecode.clipboards.ClipBoards;
import org.zbinfinn.wecode.features.Features;
import org.zbinfinn.wecode.features.commands.SpeedDialJoin;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.helpers.RenderHelper;
import org.zbinfinn.wecode.playerstate.ModeState;
import org.zbinfinn.wecode.playerstate.State;
import org.zbinfinn.wecode.plotdata.PlotDataManager;
import org.zbinfinn.wecode.util.Constants;
import org.zbinfinn.wecode.util.TextUtil;

import java.io.IOException;

public class WeCode implements ClientModInitializer {
    public static final String MOD_ID = "wecode";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final Gson GSON = new Gson();

    public static State generalState = new State();
    public static ModeState modeState;
    public static boolean isDrawingCustomTooltip = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing");

        Constants.init();
        TextUtil.init();
        Features.init();
        ClipBoards.init();
        ColorPalette.init();
        PlotDataManager.init();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Features.tick();
            ScreenHandler.tick();
        });

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            Features.tooltip(itemStack, tooltipContext, tooltipType, list, isDrawingCustomTooltip);
        });

        HudRenderCallback.EVENT.register((draw, tickCounter) -> {
            NotificationHelper.render(draw, tickCounter);
            Features.hudRender(draw, tickCounter);
        });

        WorldRenderEvents.LAST.register(event -> {
            Features.worldRenderLast(event);
            RenderHelper.worldRenderLast(event);
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Features.clientStop(client);
            try {
                ClipBoards.save();
            } catch (IOException e) {
                LOGGER.error("Failed to save color spaces");
            }
            try {
                SpeedDialJoin sdj = (SpeedDialJoin) Features.getFeature(SpeedDialJoin.class);
                sdj.save();
            } catch (IOException e) {
                LOGGER.error("Failed to save speed dial");
            }
        });

        LOGGER.info("Initialized");

    }

    public static void changeState(ModeState state) {
        Features.changeState(modeState, state);
        modeState = state;
    }

}
