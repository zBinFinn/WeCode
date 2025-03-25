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
    private static final HashMap<Class<?>, FeatureTrait> features = new HashMap<>();

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
        flint(new CodeTeleportCommand());
        flint(new OpenConfigCommand());
        flint(new FunctionSearch());
        flint(new ParamDisplay());
        flint(new SpeedDialJoin());
        flint(new ExpressionCommand());
        flint(new ColorCommand());

        feat(new DebugFeature());
        feat(new TestCommand());

        feat(new ChatMessageToNotificationFeature());

    }

    private static void flint(FeatureTrait flintFeature) {
        features.put(flintFeature.getClass(), flintFeature);
        FlintAPI.registerFeature(flintFeature);
    }

    private static void debugFeatures() {
        feat(new StateDisplay());
    }

    private static void feat(Feature feature) {
    }

    public static FeatureTrait getFeature(Class<?> clazz) {
        return features.get(clazz);
    }

}
