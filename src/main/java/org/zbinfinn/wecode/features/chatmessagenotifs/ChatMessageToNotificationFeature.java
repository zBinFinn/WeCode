package org.zbinfinn.wecode.features.chatmessagenotifs;

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
import java.util.stream.Stream;

public class ChatMessageToNotificationFeature extends Feature {
    private final HashMap<String, SuperMatcher> matchers = new HashMap<>();

    @Override
    public void activate() {
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
    public void handlePacket(Packet<?> packetIn, CallbackInfo ci) {
        if (!(packetIn instanceof GameMessageS2CPacket packet)) {
            return;
        }
        if (ci.isCancelled()) {
            return;
        }

        Text text = packet.content();
        String message = packet.content().getString();

        matchers().filter(matcher -> matcher.matches(message)).findFirst().ifPresent(matcher -> {
            NotificationHelper.sendNotification(matcher.modify(text, message), matcher.getNotificationType(), matcher.getDuration(message));
            ci.cancel();
        });

    }
}
