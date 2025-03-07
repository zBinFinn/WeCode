package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

public class GenericMatcher implements Matcher {
    private final String match;
    private final Text text;

    public GenericMatcher(String match, Text text) {
        this.match = match;
        this.text = text;
    }

    public GenericMatcher(String match) {
        this.match = match;
        this.text = Text.literal(match);
    }

    @Override
    public boolean matches(String message) {
        return message.matches(match);
    }

    @Override
    public Text modify(Text text, String message) {
        return text;
    }
}
