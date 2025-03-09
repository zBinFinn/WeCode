package org.zbinfinn.wecode.features;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.features.chatmessagenotifs.ChatMessageToNotificationFeature;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Features {
    private static final HashMap<Class<?>, Feature> features = new HashMap<>();

    public static void init() {
        features.put(FlightSpeedKeybindFeature.class, new FlightSpeedKeybindFeature());
        features.put(ShowItemTagsKeybind.class, new ShowItemTagsKeybind());
        features.put(PinItemKeybindFeature.class, new PinItemKeybindFeature());
        features.put(NotificationTestCommand.class, new NotificationTestCommand());
        features.put(ChatMessageToNotificationFeature.class, new ChatMessageToNotificationFeature());
        features.put(LagslayerDisplayFeature.class, new LagslayerDisplayFeature());
        features.put(TemplatePreviewFeature.class, new TemplatePreviewFeature());

        features.put(TestCommand.class, new TestCommand());

        features.values().forEach(Feature::activate);
    }

    public static Stream<Feature> features() {
        return features.values().stream().filter(Feature::isEnabled);
    }

    public static Feature getFeature(Class<?> clazz) {
        return features.get(clazz);
    }

    public static void tick() {
        features().forEach(Feature::tick);
        CommandSender.tick();
    }

    public static void tooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {
        features().forEach(feature -> {
            feature.tooltip(itemStack, tooltipContext, tooltipType, list);
        });
    }

    public static void hudRender(DrawContext draw, RenderTickCounter tickCounter) {
        features().forEach(feature -> {
            feature.hudRender(draw, tickCounter);
        });
    }

    public static void handlePacket(Packet<?> packet, CallbackInfo ci) {
        features().forEach((feature) -> {
            feature.handlePacket(packet, ci);
        });
    }

    public static void sentPacket(Packet<?> packet, CallbackInfo ci) {
        features().forEach((feature) -> {
            feature.sentPacket(packet, ci);
        });
    }

    public static void worldRenderLast(WorldRenderContext event) {
        features().forEach((feature) -> {
            feature.worldRenderLast(event);
        });
    }
}
