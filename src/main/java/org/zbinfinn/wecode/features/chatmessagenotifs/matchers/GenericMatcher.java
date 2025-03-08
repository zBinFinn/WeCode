package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

public class GenericMatcher extends Matcher {
    private final String match;
    private final Text text;
    private final double duration;

    public GenericMatcher(String match, Text text, double duration) {
        this.match = match;
        this.text = text;
        this.duration = duration;
    }

    public GenericMatcher(String match, Text text) {
        this(match, text, 5);
    }

    public GenericMatcher(String match, double duration) {
        this(match, Text.literal(match), duration);
    }

    public GenericMatcher(String match) {
        this(match, Text.literal(match));
    }

    @Override
    public boolean matches(String message) {
        return message.matches(match);
    }

    @Override
    public Text modify(Text text, String message) {
        return this.text;
    }
}
