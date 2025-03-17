package org.zbinfinn.wecode.features.chatmessagenotifs;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.config.Config;

public abstract class Matcher {
    public abstract boolean matches(String message);
    public abstract Text modify(Text text, String message);

    public double getDuration() {
        return Config.getConfig().DefaultNotificationDuration;
    }
}
