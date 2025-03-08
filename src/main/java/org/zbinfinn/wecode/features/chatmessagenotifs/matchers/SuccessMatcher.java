package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import org.zbinfinn.wecode.features.chatmessagenotifs.SuperMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.FlySpeedMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.GenericSuccessMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.JoinPlotMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.SwitchModeMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public class SuccessMatcher extends SuperMatcher {
    public SuccessMatcher() {
        matchers.add(new JoinPlotMatcher());
        matchers.add(new SwitchModeMatcher());
        matchers.add(new FlySpeedMatcher());

        // TODO: Remove when you've covered all cases
        matchers.add(new GenericSuccessMatcher());
    }

    @Override
    protected String trim(String message) {
        return message.substring("» ".length());
    }

    @Override
    protected boolean canTrim(String message) {
        return message.startsWith("» ");
    }

    @Override
    public NotificationHelper.NotificationType getNotificationType() {
        return NotificationHelper.NotificationType.SUCCESS;
    }
}
