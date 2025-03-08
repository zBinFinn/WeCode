package org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

public class SwitchModeMatcher extends Matcher {
    @Override
    public boolean matches(String message) {
        return message.matches("You are now in (dev|build) mode\\.");
    }

    @Override
    public Text modify(Text text, String message) {
        String trimmed = message.substring("You are now in ".length());
        if (trimmed.startsWith("dev")) {
            return Text.literal("Dev");
        }
        if (trimmed.startsWith("build")) {
            return Text.literal("Build");
        }

        return text;
    }

    @Override
    public double getDuration() {
        return 1;
    }
}
