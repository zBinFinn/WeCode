package org.zbinfinn.wecode.features.commands.expressioncommand.parser;

public class NumberExpr extends Expr {
    final String value;

    public NumberExpr(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
