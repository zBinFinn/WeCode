package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public interface SuperMatcher {
    boolean matches(String message);
    Text modify(Text text, String message);
    NotificationHelper.NotificationType getNotificationType();
}
