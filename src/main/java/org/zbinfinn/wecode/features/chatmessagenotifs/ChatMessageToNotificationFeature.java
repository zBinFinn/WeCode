package org.zbinfinn.wecode.features.chatmessagenotifs;

import dev.dfonline.flint.feature.trait.ChatListeningFeature;
import dev.dfonline.flint.util.result.ReplacementEventResult;
import net.kyori.adventure.text.Component;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.ErrorMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.SuccessMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

public class ChatMessageToNotificationFeature implements ChatListeningFeature {
    private final HashMap<String, SuperMatcher> matchers = new HashMap<>();

    public ChatMessageToNotificationFeature() {
        matchers.put("Success", new SuccessMatcher());
        matchers.put("Error", new ErrorMatcher());
    }

    private Stream<SuperMatcher> matchers() {
        ArrayList<SuperMatcher> enabledMatchers = new ArrayList<>();
        if (Config.getConfig().DFToNotifSuccess) {
            enabledMatchers.add(matchers.get("Success"));
        }
        if (Config.getConfig().DFToNotifError) {
            enabledMatchers.add(matchers.get("Error"));
        }
        return enabledMatchers.stream();
    }

    @Override
    public ReplacementEventResult<Component> onChatMessage(Text text, boolean b) {
        String message = text.getString();

        Optional<SuperMatcher> matcherOpt = matchers().filter(matcher -> matcher.matches(message)).findFirst();

        if (matcherOpt.isEmpty()) {
            return ReplacementEventResult.pass();
        }

        SuperMatcher matcher = matcherOpt.get();

        NotificationHelper.sendNotification(matcher.modify(text, message), matcher.getNotificationType(), matcher.getDuration(message));
        return ReplacementEventResult.cancel();
    }
}
