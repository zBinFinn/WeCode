package org.zbinfinn.wecode.template_editor.refactor;

import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;

import java.util.ArrayList;

public abstract class AbstractTokenizer extends Reader<Character> {
    private final String data;
    protected final ArrayList<Token> tokens = new ArrayList<>();

    public AbstractTokenizer(String data) {
        this.data = data;
    }

    protected void addToken(Token token) {
        tokens.add(token);
    }

    @Override
    protected Character getElementAt(int index) {
        return data.charAt(index);
    }

    @Override
    protected boolean hasIndex(int index) {
        return index < data.length();
    }

    protected boolean word(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (!canPeek(i)) {
                return false;
            }
            char c = peek(i);
            if (c != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public record ConsumeResult(String data, boolean foundEnd) {}
    protected void consumePrefixedUntil(int prefixLength, char end, TokenType type) {
        StringBuilder prefixB = new StringBuilder();
        for (int i = 0; i < prefixLength; i++) {
            prefixB.append(consume());
        }
        String prefix = prefixB.toString();
        ConsumeResult result = consumeUntil(end);
        String value = result.data;
        String literal = prefix + value + (result.foundEnd ? consume() : "");
        addToken(new Token(literal, value, type));
    }
    protected void consumePrefixedUntil(String prefix, char end, TokenType type) {
        consumePrefixedUntil(prefix.length(), end, type);
    }
    protected void consumePrefixedUntil(char prefix, char end, TokenType type) {
        consumePrefixedUntil(1, end, type);
    }

    protected ConsumeResult consumeUntil(char end) {
        StringBuilder buf = new StringBuilder();
        while (canPeek() && peek() != end && peek() != '\n') {
            buf.append(consume());
        }
        boolean foundEnd = false;
        if (end == '\n' && canPeek()) {
            foundEnd = true;
        } else if (canPeek() && peek() != '\n') {
            foundEnd = true;
        }
        return new ConsumeResult(buf.toString(), foundEnd);
    }

    protected boolean peekAndConsume(char c) {
        if (!canPeek()) {
            return false;
        }
        if (peek() == c) {
            consume();
            return true;
        }
        return false;
    }
}
