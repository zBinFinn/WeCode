package org.zbinfinn.wecode;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public class WeCode implements ClientModInitializer {
    public static final String MOD_ID = "wecode";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient MC = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing");

        Features.init();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Features.tick();
        });

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            Features.tooltip(itemStack, tooltipContext, tooltipType, list);
        });

        HudRenderCallback.EVENT.register((draw, tickCounter) -> {
            NotificationHelper.render(draw, tickCounter);
            Features.hudRender(draw, tickCounter);
        });


        LOGGER.info("Initialized");
    }

}
