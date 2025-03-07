package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.SuperMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.GenericSuccessMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.JoinPlotMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.SwitchModeMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;

public class SuccessMatcher implements SuperMatcher {
    private final ArrayList<Matcher> matchers = new ArrayList<Matcher>();

    public SuccessMatcher() {
        matchers.add(new JoinPlotMatcher());
        matchers.add(new SwitchModeMatcher());

        // TODO: Remove when you've covered all cases
        matchers.add(new GenericSuccessMatcher());
    }

    private String trimMessage(String message) {
        String trimmed = message.substring("» ".length());
        return trimmed;
    }

    @Override
    public boolean matches(String message) {
        if (!message.startsWith("» ")) {
            return false;
        }
        message = trimMessage(message);

        boolean matching = false;
        for (Matcher matcher : matchers) {
            if (matcher.matches(message)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Text modify(Text text, String message) {
        message = trimMessage(message);

        for (Matcher matcher : matchers) {
            if (matcher.matches(message)) {
                return matcher.modify(text, message);
            }
        }

        return Text.literal("Failed to match: ").append(text);
    }

    @Override
    public NotificationHelper.NotificationType getNotificationType() {
        return NotificationHelper.NotificationType.SUCCESS;
    }
}
