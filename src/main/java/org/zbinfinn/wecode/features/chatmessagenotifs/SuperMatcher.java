package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class SuperMatcher {
    protected ArrayList<Matcher> matchers = new ArrayList<Matcher>();
    protected Set<String> literalMatches = new HashSet<String>();
    protected Set<String> regexMatches = new HashSet<>();

    protected abstract String trim(String message);
    protected abstract boolean canTrim(String message);

    public boolean matches(String message) {
        if (!canTrim(message)) {
            return false;
        }

        message = trim(message);

        if (literalMatches.contains(message)) {
            return true;
        }

        for (String regex : regexMatches) {
            if (message.matches(regex)) {
                return true;
            }
        }

        for (Matcher matcher : matchers) {
            if (matcher.matches(message)) {
                return true;
            }
        }

        return false;
    }

    public Text modify(Text text, String message) {
        message = trim(message);

        if (literalMatches.contains(message)) {
            return Text.literal(message);
        }

        for (String regex : regexMatches) {
            if (message.matches(regex)) {
                return Text.literal(message);
            }
        }

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
        return Config.getConfig().DefaultNotificationDuration;
    }

    public abstract NotificationHelper.NotificationType getNotificationType();
}
