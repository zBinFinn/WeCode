package org.zbinfinn.wecode.template_editor.token;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.template_editor.TEColor;

import java.util.regex.Pattern;

public class Token {
    public final String value;
    public final String text;
    public final TokenType type;
    private int valueLength;
    private int prefixLength;
    private int suffixLength;
    private boolean cachedLengths = false;
    public Token(String value, TokenType type) {
        this(value, value, type);
    }
    public Token(String text, String value, TokenType type) {
        this.value = value;
        this.type = type;
        this.text = text;
    }
    public Token(char ch, TokenType type) {
        this(String.valueOf(ch), type);
    }

    @Override
    public String toString() {
        return switch (type) {
            case STRING_LIT -> '"' + value + '"';
            case TEXT_LIT -> "S\"" + value + '"';
            default -> value;
        };
    }

    public Text toText() {
        int color = TEColor.fromType(type);
        return Text.literal(text).withColor(color);
    }

    public String debugString() {
        return switch (type) {
            case EOL -> type.name();
            default -> type.name() + ": '" + value + "'";
        };
    }

    public int lengthWithoutSuffix() {
        if (!cachedLengths) {
            String[] split = text.split(Pattern.quote(value));
            valueLength = value.length();
            prefixLength = (split.length >= 1) ? split[0].length() : 0;
            suffixLength = (split.length >= 2) ? split[1].length() : 0;
            cachedLengths = true;
        }
        return prefixLength + valueLength;
    }
}
