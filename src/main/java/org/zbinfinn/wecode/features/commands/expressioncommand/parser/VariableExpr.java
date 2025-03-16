package org.zbinfinn.wecode.features.commands.expressioncommand.parser;

public class VariableExpr extends Expr {
    final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "%var(" + name + ")";
    }
}
