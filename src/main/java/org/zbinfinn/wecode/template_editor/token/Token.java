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
        return switch(type) {
            case EXPRESSION_LIT -> Text
                .literal("E'").withColor(TEColor.EXPRESSION_OUTSIDE.value())
                .append(Text.literal(value).withColor(TEColor.EXPRESSION_INSIDE.value()))
                .append(Text.literal(text.endsWith("'") ? "'" : "").withColor(TEColor.EXPRESSION_OUTSIDE.value()));
            default -> Text.literal(text).withColor(color);
        };
    }

    public String debugString() {
        return switch (type) {
            case EOL -> type.name();
            default -> type.name() + ": '" + value + "'";
        };
    }

    public int lengthWithoutSuffix() {
        if (!cachedLengths) {
            prefixLength = switch (type) {
                case TAG_LIT -> 2;
                case ACTION -> (text.startsWith("'")) ? 1 : 0;
                default -> -1;
            };
            valueLength = value.length();
            cachedLengths = true;
        }
        System.out.println("Prefix " + prefixLength + " Value " + valueLength);
        return prefixLength + valueLength;
    }
}
