package org.zbinfinn.wecode.features.commands.expressioncommand.parser;

public class BinaryExpr extends Expr {
    final Expr left;
    final String operator;
    final Expr right;
    boolean wrappedInMath = false;

    public BinaryExpr(Expr left, String operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return ((wrappedInMath) ? "%math(" : "") + left + " " + operator + " " + right + ((wrappedInMath) ? ")" : "");
    }
}
