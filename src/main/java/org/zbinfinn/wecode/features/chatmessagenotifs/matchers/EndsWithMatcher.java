package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

public class EndsWithMatcher extends Matcher {
    private String endsWith;
    public EndsWithMatcher(String endsWith) {
        this.endsWith = endsWith;
    }

    @Override
    public boolean matches(String message) {
        return message.endsWith(endsWith);
    }

    @Override
    public Text modify(Text text, String message) {
        return Text.literal(message);
    }
}
