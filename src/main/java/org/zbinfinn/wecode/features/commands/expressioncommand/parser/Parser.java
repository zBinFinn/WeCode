package org.zbinfinn.wecode.features.commands.expressioncommand.parser;

import org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer.Token;
import org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer.TokenType;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expr parseExpression() {
        Expr expr = parseTerm(); // Start by parsing the first term (multiplication or division)

        while (check(TokenType.OPERATOR) && (peek().value.equals("+") || peek().value.equals("-"))) {
            String operator = peek().value;
            advance();
            Expr right = parseTerm(); // Recursively parse the next term
            expr = new BinaryExpr(expr, operator, right); // Combine the left and right parts
        }

        return expr;
    }

    public String mathExpression() {
        Expr expr = parseExpression();
        return (expr instanceof BinaryExpr bin && bin.wrappedInMath) ? expr.toString() : "%math(" + expr + ")";
    }

    private Expr parseTerm() {
        Expr expr = parseFactor(); // Start by parsing the first factor

        while (check(TokenType.OPERATOR) && (peek().value.equals("*") || peek().value.equals("/"))) {
            String operator = peek().value;
            advance();
            Expr right = parseFactor(); // Recursively parse the right side (next factor)
            expr = new BinaryExpr(expr, operator, right); // Combine the left and right parts
        }

        if (expr instanceof BinaryExpr bin) {
            bin.wrappedInMath = true;
            return bin;
        }

        return expr;
    }

    private Expr parseFactor() {
        if (match(TokenType.NUMBER)) {
            return new NumberExpr(previous().value);
        }

        if (match(TokenType.VARIABLE)) {
            return new VariableExpr(previous().value);
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = parseExpression();
            if (!match(TokenType.RIGHT_PAREN)) {
                throw new RuntimeException("Expected ')' after expression");
            }
            if (expr instanceof BinaryExpr bin) {
                bin.wrappedInMath = true;
                return bin;
            }
            return expr;
        }

        throw new RuntimeException("Unexpected token: " + peek());
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }
    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type == type;
    }
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    private Token peek() {
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }
    private boolean isAtEnd() {
        return current >= tokens.size();
    }
}
