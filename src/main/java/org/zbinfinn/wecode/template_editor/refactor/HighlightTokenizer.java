package org.zbinfinn.wecode.template_editor.refactor;

import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HighlightTokenizer extends AbstractTokenizer {
    private static final Map<Character, TokenType> PEEK_AND_CONSUME_MAP;

    static {
        PEEK_AND_CONSUME_MAP = new HashMap<>();
        PEEK_AND_CONSUME_MAP.put('{', TokenType.OPEN_CURLY);
        PEEK_AND_CONSUME_MAP.put('}', TokenType.CLOSE_CURLY);
        PEEK_AND_CONSUME_MAP.put('\n', TokenType.EOL);
    }

    private static class State {
        boolean openParen = false;
        boolean ignoreSpaces = false;
    }

    private State state = new State();

    public HighlightTokenizer(String data) {
        super(data);
    }

    public ArrayList<Token> tokenize(boolean ignoreSpaces) {
        reset();
        state = new State();
        state.ignoreSpaces = ignoreSpaces;
        tokens.clear();
        while (canPeek()) {
            run();
        }
        return tokens;
    }

    private void run() {
        for (Map.Entry<Character, TokenType> entry : PEEK_AND_CONSUME_MAP.entrySet()) {
            if (peekAndConsume(entry.getKey())) {
                addToken(new Token(entry.getKey(), entry.getValue()));
                return;
            }
        }
        if (peekAndConsume(' ')) {
            if (!state.ignoreSpaces) {
                addToken(new Token(' ', TokenType.SPACE));
            }
            return;
        }
        if (peekAndConsume('(')) {
            addToken(new Token('(', TokenType.OPEN_PAREN));
            state.openParen = true;
            return;
        }
        if (peekAndConsume(')')) {
            addToken(new Token(')', TokenType.CLOSE_PAREN));
            state.openParen = false;
            return;
        }
        if (word("//")) {
            ConsumeResult result = consumeUntil('\n');
            addToken(new Token(result.data(), TokenType.COMMENT));
            return;
        }
        if (Character.isDigit(peek())) {
            parseNumber();
            return;
        }
        if (peek() == '\"') {
            consumePrefixedUntil("\"", '\"', TokenType.STRING_LIT);
            return;
        }
        if (peek() == '\'') {
            consumePrefixedUntil('\'', '\'', TokenType.ACTION);
            return;
        }
        if (peek() == '<') {
            consumePrefixedUntil('<', '>', state.openParen ? TokenType.VECTOR_LIT : TokenType.TARGET);
            return;
        }
        if (peek() == '!') {
            consumePrefixedUntil('!', '!', TokenType.EMPTY_ARGUMENTS);
            return;
        }
        if (word("G\"")) {
            consumePrefixedUntil("G\"", '"', TokenType.GAME_VALUE_LIT);
            return;
        }
        if (word("P'")) {
            consumePrefixedUntil("P'", '\'', TokenType.PARAMETER_LIT);
            return;
        }
        if (word("POT\"")) {
            consumePrefixedUntil("POT\"", '"', TokenType.POTION_LIT);
            return;
        }
        if (word("L\"")) {
            consumePrefixedUntil("L\"", '"', TokenType.LOCATION_LIT);
            return;
        }
        if (word("PART\"")) {
            parseParticle();
            return;
        }
        if (word("I\"")) {
            parseItem();
            return;
        }
        if (word("S\"")) {
            consumePrefixedUntil("S\"", '"', TokenType.SOUND_LIT);
            return;
        }
        if (word("H\"")) {
            consumePrefixedUntil("H\"", '"', TokenType.HINT_LIT);
            return;
        }
        if (word("$\"")) {
            consumePrefixedUntil("$\"", '"', TokenType.TEXT_LIT);
            return;
        }
        if (word("T\"")) {
            consumePrefixedUntil("T\"", '"', TokenType.TAG_LIT);
            return;
        }
        if (peek() == '[') {
            parseVariable();
        }
        if (Character.isAlphabetic(peek())) {
            if (state.openParen) {
                parseVariable();
            } else {
                parseAction();
            }
        }

        addToken(new Token(consume(), TokenType.PLAIN));
    }

    private void parseVariable() {
        if (peek() == '[') {
            consume();
            StringBuilder buf = new StringBuilder();
            while (canPeek() && peek() != ']') {
                buf.append(consume());
            }
            String varName = buf.toString();
            if (canPeek()) {
                consume();

                String postfix = getVariablePostfix();

                String extra = "";
                if (!postfix.equals("li")) {
                    extra = "@" + postfix;
                    consume();
                }

                tokens.add(new Token("[" + varName + "]" + extra, varName + "@" + postfix, TokenType.VARIABLE));
                return;
            }
            tokens.add(new Token("[" + varName, varName + "@li", TokenType.VARIABLE));
            return;
        }
        StringBuilder buf = new StringBuilder();
        while (canPeek() && peek() != ')' && peek() != ' ') {
            buf.append(consume());
        }
        String varName = buf.toString();
        String realName = varName;
        if (!varName.endsWith("@s") && !varName.endsWith("@g") && !varName.endsWith("@i")) {
            realName = realName + "@li";
        }
        addToken(new Token(varName, realName, TokenType.VARIABLE));
    }

    private String getVariablePostfix() {
        String postfix = "li";
        if (canPeek(2)) {
            if (peek() == '@') {
                consume();
                postfix = switch (peek()) {
                    case 's' -> "s";
                    case 'l' -> "l";
                    case 'g' -> "g";
                    default -> postfix;
                };
            }
        }
        return postfix;
    }

    private void parseAction() {
        StringBuilder buf = new StringBuilder();
        do {
            buf.append(consume());
        } while (canPeek() && (Character.isAlphabetic(peek()) || Character.isDigit(peek())));

        String string = buf.toString();
        if (TemplateConstants.ACTION_SPECIFIERS.containsKey(string)) {
            addToken(new Token(string, TokenType.ACTION_TYPE));
            return;
        }

        if (string.equals("NOT")) {
            addToken(new Token(string, TokenType.ATTRIBUTE_NOT));
            return;
        }

        addToken(new Token(string, TokenType.ACTION));
    }

    private void parseParticle() {
        consume(5);
        StringBuilder buf = new StringBuilder();
        int openBrackets = 0;
        while (canPeek()) {
            if (openBrackets <= 0 && peek() == '"') {
                break;
            }
            if (peek() == '{') openBrackets++;
            if (peek() == '}') openBrackets--;
            buf.append(consume());
        }
        String value = buf.toString();
        String literal = "PART\"" + value;
        if (canPeek()) {
            consume();
            literal += "\"";
        }
        addToken(new Token(literal, value, TokenType.PARTICLE_LIT));
    }

    private void parseItem() {
        consume(2);
        StringBuilder buf = new StringBuilder();
        int openBrackets = 0;
        while (canPeek()) {
            if (openBrackets <= 0 && peek() == '"') {
                break;
            }
            if (peek() == '{') openBrackets++;
            if (peek() == '}') openBrackets--;
            buf.append(consume());
        }
        String value = buf.toString();
        String literal = "I\"" + value;
        if (canPeek()) {
            consume();
            literal += "\"";
        }
        addToken(new Token(literal, value, TokenType.ITEM_LIT));
    }

    private void parseNumber() {
        StringBuilder buf = new StringBuilder();
        int dots = 0;
        while (canPeek()) {
            if (peek() == '.') {
                dots++;
            }
            buf.append(consume());
        }
        String value = buf.toString();
        if (dots > 1) {
            addToken(new Token(value, TokenType.PLAIN));
        }
        addToken(new Token(value, TokenType.NUMBER_LIT));
    }
}
