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

    public static GenericMatcher gen(String message) {
        return gen(message, null);
    }
    public static GenericMatcher gen(String oldMessage, String newMessage) {
        return gen(oldMessage, newMessage, 5);
    }
    public static GenericMatcher gen(String message, int duration) {
        return gen(message, null, duration);
    }
    public static GenericMatcher gen(String oldMessage, String newMessage, int duration) {
        return new GenericMatcher(oldMessage, (newMessage == null) ? null : Text.literal(newMessage), duration);
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
        if (this.text != null) {
            return this.text;
        }
        return Text.literal(message);
    }
}
