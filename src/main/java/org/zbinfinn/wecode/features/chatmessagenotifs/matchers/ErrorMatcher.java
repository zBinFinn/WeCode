package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.SuperMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.error.GenericErrorMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.JoinPlotMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;

public class ErrorMatcher implements SuperMatcher {
    private final ArrayList<Matcher> matchers = new ArrayList<Matcher>();

    public ErrorMatcher() {
        matchers.add(new GenericMatcher("Could not find that player."));

        // TODO: Remove when you've covered all cases
        matchers.add(new GenericErrorMatcher());
    }

    @Override
    public boolean matches(String message) {
        if (!message.startsWith("Error: ")) {
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
        return NotificationHelper.NotificationType.ERROR;
    }

    private String trimMessage(String message) {
        String trimmed = message.substring("Error: ".length());
        return trimmed;
    }
}
