package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;

public abstract class SuperMatcher {
    protected ArrayList<Matcher> matchers = new ArrayList<Matcher>();

    protected abstract String trim(String message);
    protected abstract boolean canTrim(String message);

    public boolean matches(String message) {
        if (!canTrim(message)) {
            return false;
        }

        message = trim(message);
        for (Matcher matcher : matchers) {
            if (matcher.matches(message)) {
                return true;
            }
        }

        return false;
    }

    public Text modify(Text text, String message) {
        message = trim(message);
        for (Matcher matcher : matchers) {
            if (matcher.matches(message)) {
                return matcher.modify(text, message);
            }
        }

        return Text.literal("Failed to parse message");
    }

    public double getDuration(String message) {
        message = trim(message);
        for (Matcher matcher : matchers) {
            if (matcher.matches(message)) {
                return matcher.getDuration();
            }
        }
        return 5;
    }

    public abstract NotificationHelper.NotificationType getNotificationType();
}
