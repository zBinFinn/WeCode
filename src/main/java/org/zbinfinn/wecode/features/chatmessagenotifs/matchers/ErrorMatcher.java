package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import org.zbinfinn.wecode.features.chatmessagenotifs.SuperMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.error.GenericErrorMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public class ErrorMatcher extends SuperMatcher {
    public ErrorMatcher() {
        matchers.add(new GenericMatcher("Could not find that player."));

        // TODO: Remove when you've covered all cases
        matchers.add(new GenericErrorMatcher());
    }

    @Override
    protected String trim(String message) {
        return message.substring("Error: ".length());
    }

    @Override
    protected boolean canTrim(String message) {
        return message.startsWith("Error: ");
    }

    @Override
    public NotificationHelper.NotificationType getNotificationType() {
        return NotificationHelper.NotificationType.ERROR;
    }
}
