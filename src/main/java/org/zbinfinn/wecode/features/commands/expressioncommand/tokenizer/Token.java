package org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer;

public class Token {
    public TokenType type;
    public String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{ type=" + type + ", value='" + value + "' }";
    }
}
