package org.zbinfinn.wecode.features;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
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
import org.zbinfinn.wecode.features.commands.ColorSpaceCommands;
import org.zbinfinn.wecode.features.commands.*;
import org.zbinfinn.wecode.features.commands.targetedjoincommands.BuildIDCommand;
import org.zbinfinn.wecode.features.commands.CodeTeleportCommand;
import org.zbinfinn.wecode.features.commands.targetedjoincommands.DevIDCommand;
import org.zbinfinn.wecode.features.keybinds.FlightSpeedKeybindFeature;
import org.zbinfinn.wecode.features.keybinds.PinItemKeybindFeature;
import org.zbinfinn.wecode.features.keybinds.ShowItemTagsKeybind;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Features {
    private static final HashMap<Class<?>, Feature> features = new HashMap<>();

    public static void init() {
        feat(new FlightSpeedKeybindFeature());
        feat(new ShowItemTagsKeybind());
        feat(new PinItemKeybindFeature());
        feat(new NotificationTestCommand());
        feat(new BuildIDCommand());
        feat(new DevIDCommand());
        feat(new LagslayerDisplayFeature());
        feat(new TemplatePreviewFeature());
        feat(new BatchTagCommand());
        feat(new SmallCapsCommand());
        feat(new ColorSpaceCommands());
        feat(new ColorSpaceApplicator());
        feat(new PlayerJoinCommand());
        feat(new ChatMessageToNotificationFeature());
        feat(new SaveLoadInvCommand());
        feat(new CodeTeleportCommand());
        feat(new OpenConfigCommand());
        feat(new MiddleClickSingleFeature());

        feat(new TestCommand());

        features.values().forEach(Feature::activate);
    }

    private static void feat(Feature feature) {
        features.put(feature.getClass(), feature);
    }

    public static Stream<Feature> features() {
        return features.values().stream().filter(Feature::isEnabled);
    }
    public static Stream<ChestFeature> chestFeatures() {
        return features().map(Feature::getChestFeature).filter(Optional::isPresent).map(Optional::get);
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

    public static void clientStop(MinecraftClient client) {
        features().forEach((feature) -> {
            feature.clientStop(client);
        });
    }

    public static String handleChatMessage(String message) {
        for (Feature feature : features().toList()) {
            message = feature.handleChatMessage(message);
        }
        return message;
    }
}
