package org.zbinfinn.wecode.features;

import dev.dfonline.flint.FlintAPI;
import dev.dfonline.flint.feature.core.FeatureTrait;
import org.zbinfinn.wecode.features.chatmessagenotifs.ChatMessageToNotificationFeature;
import org.zbinfinn.wecode.features.commands.*;
import org.zbinfinn.wecode.features.commands.expressioncommand.ExpressionCommand;
import org.zbinfinn.wecode.features.commands.targetedjoincommands.BuildIDCommand;
import org.zbinfinn.wecode.features.commands.targetedjoincommands.DevIDCommand;
import org.zbinfinn.wecode.features.debug.StateDisplay;
import org.zbinfinn.wecode.features.functionsearch.FunctionSearch;
import org.zbinfinn.wecode.features.keybinds.FlightSpeedKeyBindFeature;
import org.zbinfinn.wecode.features.keybinds.PinItemKeybindFeature;
import org.zbinfinn.wecode.features.keybinds.ShowItemTagsKeybind;
import org.zbinfinn.wecode.template_editor.TemplateEditorCommands;

import java.util.HashMap;

public class Features {
    private static final HashMap<Class<?>, FeatureTrait> features = new HashMap<>();

    public static void init() {
        debugFeatures();

        flint(new TemplateEditorCommands());

        flint(new AutoChatFeature());
        flint(new MessageCommands());
        flint(new ShowItemTagsKeybind());
        flint(new FlightSpeedKeyBindFeature());
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
        flint(new ChatMessageToNotificationFeature());
        flint(new DisplayLoreWhenEditingFeature());
    }

    private static void flint(FeatureTrait flintFeature) {
        features.put(flintFeature.getClass(), flintFeature);
        FlintAPI.registerFeature(flintFeature);
    }

    private static void debugFeatures() {
        flint(new StateDisplay());
    }

    private static void feat(Feature feature) {
    }

    public static FeatureTrait getFeature(Class<?> clazz) {
        return features.get(clazz);
    }

}
