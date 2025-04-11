package org.zbinfinn.wecode.template_editor.token;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.template_editor.TEColor;

public class Token {
    public final String value;
    public final String text;
    public final TokenType type;
    public Token(String value, TokenType type) {
        this.value = value;
        this.text = value;
        this.type = type;
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
}
