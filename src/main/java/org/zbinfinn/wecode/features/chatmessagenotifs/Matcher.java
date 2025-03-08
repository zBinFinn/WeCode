package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public abstract class Matcher {
    public abstract boolean matches(String message);
    public abstract Text modify(Text text, String message);

    public double getDuration() {
        return 5;
    }
}
