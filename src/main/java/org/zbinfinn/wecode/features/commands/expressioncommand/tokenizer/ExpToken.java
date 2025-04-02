package org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer;

public class ExpToken {
    public TokenType type;
    public String value;

    ExpToken(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{ type=" + type + ", value='" + value + "' }";
    }
}
