package org.zbinfinn.wecode.features;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.FlintAPI;
import dev.dfonline.flint.feature.core.FeatureTrait;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.chatmessagenotifs.ChatMessageToNotificationFeature;
import org.zbinfinn.wecode.features.commands.*;
import org.zbinfinn.wecode.features.commands.expressioncommand.ExpressionCommand;
import org.zbinfinn.wecode.features.commands.targetedjoincommands.BuildIDCommand;
import org.zbinfinn.wecode.features.commands.targetedjoincommands.DevIDCommand;
import org.zbinfinn.wecode.features.debug.StateDisplay;
import org.zbinfinn.wecode.features.functionsearch.FunctionSearch;
import org.zbinfinn.wecode.features.keybinds.FlightSpeedKeybindFeature;
import org.zbinfinn.wecode.features.keybinds.PinItemKeybindFeature;
import org.zbinfinn.wecode.features.keybinds.ShowItemTagsKeybind;
import org.zbinfinn.wecode.playerstate.ModeState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Features {
    private static final HashMap<Class<?>, Feature> features = new HashMap<>();

    public static void init() {
        debugFeatures();

        flint(new FlightSpeedKeybindFeature());
        flint(new AutoChatFeature());
        flint(new MessageCommands());
        flint(new ShowItemTagsKeybind());
        flint(new PinItemKeybindFeature());
        flint(new NotificationTestCommand());
        flint(new BuildIDCommand());
        flint(new DevIDCommand());
        flint(new LagslayerDisplayFeature());
        flint(new TemplatePreviewFeature());
        flint(new BatchTagCommand());
        flint(new SmallCapsCommand());
        flint(new ColorSpaceCommands());
        flint(new ColorSpaceApplicator());
        flint(new PlayerJoinCommand());
        flint(new SaveLoadInvCommand());

        feat(new CodeTeleportCommand());
        feat(new OpenConfigCommand());
        feat(new FunctionSearch());
        feat(new ParamDisplay());
        feat(new SpeedDialJoin());
        feat(new ExpressionCommand());
        feat(new ColorCommand());

        feat(new CachePlotDataCommand());
        feat(new DebugFeature());
        feat(new TestCommand());

        feat(new ChatMessageToNotificationFeature());

        features.values().stream().filter(feature -> feature instanceof CommandFeature).forEach(feature -> {
            ((CommandFeature) feature).commandActivate();
        });
        features.values().forEach(Feature::activate);
    }

    private static void flint(FeatureTrait flintFeature) {
        FlintAPI.registerFeature(flintFeature);
    }

    private static void debugFeatures() {
        feat(new StateDisplay());
    }

    private static void feat(Feature feature) {
        features.put(feature.getClass(), feature);
    }

    public static Stream<Feature> features() {
        return features.values().stream().filter(Feature::isEnabled);
    }

    public static Feature getFeature(Class<?> clazz) {
        return features.get(clazz);
    }

    public static void tick() {
        if (WeCode.MC.player == null) {
            return;
        }
        features().forEach(Feature::tick);
        CommandSender.tick();
    }

    public static void tooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list, boolean isCustom) {
        features().forEach(feature -> {
            feature.tooltip(itemStack, tooltipContext, tooltipType, list, isCustom);
        });
    }

    public static void hudRender(DrawContext draw, RenderTickCounter tickCounter) {
        features().forEach(feature -> {
            feature.hudRender(draw, tickCounter);
        });
    }

    public static void handlePacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof GameMessageS2CPacket messagePacket) {
            receiveChatMessage(messagePacket, ci);
        }
        for (Feature feature : features().toList()) {
            feature.handlePacket(packet, ci);
        }
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

    public static void receiveChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        for (Feature feature : features().toList()) {
            if (!packet.overlay()) {
                feature.receiveChatMessage(packet, ci);
            }
        }
    }

    public static void changeState(ModeState oldState, ModeState newState) {
        for (Feature feature : features().toList()) {
            feature.changeState(oldState, newState);
        }
    }
}
