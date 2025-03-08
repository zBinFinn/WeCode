package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.ErrorMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.SuccessMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;

public class ChatMessageToNotificationFeature extends Feature {
    private final ArrayList<SuperMatcher> matchers = new ArrayList<>();

    @Override
    public void activate() {
        matchers.add(new ErrorMatcher());
        matchers.add(new SuccessMatcher());
    }

    @Override
    public void handlePacket(Packet<?> packetIn, CallbackInfo ci) {
        if (!(packetIn instanceof GameMessageS2CPacket packet)) {
            return;
        }

        Text text = packet.content();
        String message = packet.content().getString();

        matchers.stream().filter(matcher -> matcher.matches(message)).findFirst().ifPresent(matcher -> {
            NotificationHelper.sendNotification(matcher.modify(text, message), matcher.getNotificationType(), matcher.getDuration(message));
            ci.cancel();
        });

    }
}
