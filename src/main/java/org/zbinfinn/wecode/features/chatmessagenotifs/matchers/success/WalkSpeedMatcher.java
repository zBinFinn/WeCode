package org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success;

import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

import java.util.List;

public class WalkSpeedMatcher extends Matcher {
    @Override
    public boolean matches(String message) {
        @RegExp String regex = "Set walk speed to: \\d\\d?\\d?% of default speed\\.";
        return message.matches(regex);
    }

    @Override
    public Text modify(Text text, String message) {
        List<Text> siblings = text.getSiblings();
        Text sibling = siblings.get(1);
        String siblingStr = sibling.getString();
        int flySpeed = Integer.parseInt(siblingStr.substring("Set walk speed to: ".length()));
        return Text.literal("Walk Speed: " + flySpeed + "%");
    }

    @Override
    public double getDuration() {
        return 2;
    }
}

