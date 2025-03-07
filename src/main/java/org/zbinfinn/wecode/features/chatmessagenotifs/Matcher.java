package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public interface Matcher {
    boolean matches(String message);
    Text modify(Text text, String message);
}
