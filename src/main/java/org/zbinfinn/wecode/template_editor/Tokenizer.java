package org.zbinfinn.wecode.template_editor;

import org.spongepowered.include.com.google.common.collect.HashBiMap;
import org.zbinfinn.wecode.template_editor.token.*;

import java.util.*;

public class Tokenizer {
    /*
    private static final Map<Character, TokenType> CHECK_AND_ADD_MAP;
    private boolean highlighting;

    static {
        CHECK_AND_ADD_MAP = new HashMap<>();
        CHECK_AND_ADD_MAP.put('(', TokenType.OPEN_PAREN);
        CHECK_AND_ADD_MAP.put(')', TokenType.CLOSE_PAREN);
        CHECK_AND_ADD_MAP.put('{', TokenType.OPEN_CURLY);
        CHECK_AND_ADD_MAP.put('}', TokenType.CLOSE_CURLY);
        CHECK_AND_ADD_MAP.put(' ', TokenType.SPACE);
    }

    private final String text;
    private int index = 0;
    private final List<Token> tokens = new ArrayList<>();
    private boolean hasParsedBracketOpen = false;

    public Tokenizer(String text) {
        this.text = text;
    }

    public List<Token> tokenize(boolean highlighting) {
        this.highlighting = highlighting;
        while (peekOpt().isPresent()) {
            boolean shouldContinue = false;
            if (!highlighting && peek() == '\n') {
                tokens.add(new Token(consume(), TokenType.EOL));
                hasParsedBracketOpen = false;

                continue;
            }
            for (Map.Entry<Character, TokenType> entry : CHECK_AND_ADD_MAP.entrySet()) {
                if (checkAndAdd(entry.getKey(), entry.getValue())) {
                    shouldContinue = true;
                    break;
                }
            }
            if (shouldContinue) {
                continue;
            } else if (Character.isAlphabetic(peek())) {
                if (hasParsedBracketOpen) {
                    parseVariable();
                } else {
                    parseAction();
                }
            } else {
                tokens.add(new Token(consume(), TokenType.PLAIN));
            }
        }

        if (!highlighting) {
            tokens.add(new Token("\n", TokenType.EOL));
        }

        return tokens;
    }

    private void parseGameValueLiteral() {
        consume();
        consume();
        String value = consumeUntil("\"");
        String literal = "G\"" + value;
        if (peekOpt().isPresent()) {
            consume();
            literal += "\"";
        }
        tokens.add(new Token(literal, value, TokenType.GAME_VALUE_LIT));
    }

    private void parseParameterLiteral() {
        consume();
        consume();
        String value = consumeUntil("'");
        String literal = "P'" + value;
        if (peekOpt().isPresent()) {
            consume();
            literal += "'";
        }
        tokens.add(new Token(literal, value, TokenType.PARAMETER_LIT));
    }

    private void parsePotionLiteral() {
        consume();
        consume();
        consume();
        consume();
        String value = consumeUntil("\"");
        String literal = "POT\"" + value;
        if (peekOpt().isPresent()) {
            consume();
            literal += "\"";
        }
        tokens.add(new Token(literal, value, TokenType.POTION_LIT));
    }

    private void parseParticleLiteral() {
        consume();
        consume();
        consume();
        consume();
        consume();
        StringBuilder buf = new StringBuilder();
        int openBrackets = 0;
        while (peekOpt().isPresent()) {
            if (openBrackets <= 0 && peek() == '"') {
                break;
            }
            if (peek() == '{') openBrackets++;
            if (peek() == '}') openBrackets--;
            buf.append(consume());
        }
        String value = buf.toString();
        String literal = "PART\"" + value;
        if (peekOpt().isPresent()) {
            consume();
            literal += "\"";
        }
        tokens.add(new Token(literal, value, TokenType.PARTICLE_LIT));
    }

    private String consumeUntil(String phrase) {
        StringBuilder buf = new StringBuilder();
        while (peekOpt(phrase.length() - 1).isPresent() && !phrase(phrase)) {
            buf.append(consume());
        }
        return buf.toString();
    }

    private void parseSoundLiteral() {
        consume();
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '"') {
            buf.append(consume());
        }
        String literal = "S\"" + buf;
        if (peekOpt().isPresent()) {
            consume();
            literal = literal + "\"";
        }
        tokens.add(new Token(literal, buf.toString(), TokenType.SOUND_LIT));
    }

    private void parseItemLit() {
        consume();
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peekOpt(1).isPresent() && (peek(1) != 'I' || peek() != '|')) {
            buf.append(consume());
        }
        String literal = "I|" + buf;
        if (peekOpt().isPresent()) {
            consume();
            literal = literal + "|";
            if (peekOpt().isPresent()) {
                consume();
                literal = literal + "I";
            }
        }
        tokens.add(new Token(literal, buf.toString(), TokenType.ITEM_LIT));
    }

    private void parseVectorLit() {
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '>') {
            buf.append(consume());
        }
        if (peekOpt().isPresent()) {
            consume();
            tokens.add(new Token("<" + buf + ">", buf.toString(), TokenType.VECTOR_LIT));
            return;
        }
        tokens.add(new Token("<" + buf, buf.toString(), TokenType.VECTOR_LIT));
    }

    private void parseLocationLit() {
        consume();
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '"') {
            buf.append(consume());
        }
        if (peekOpt().isPresent()) {
            consume();
            tokens.add(new Token("L\"" + buf + "\"", buf.toString(), TokenType.LOCATION_LIT));
            return;
        }
        tokens.add(new Token("L\"" + buf, buf.toString(), TokenType.LOCATION_LIT));
    }

    private void parseEmptyArguments() {
        consume();
        StringBuilder amount = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '!') {
            amount.append(consume());
        }
        if (peekOpt().isPresent()) {
            consume();
            tokens.add(new Token("!" + amount + "!", amount.toString(), TokenType.EMPTY_ARGUMENTS));
            return;
        }
        tokens.add(new Token("!" + amount, amount.toString(), TokenType.EMPTY_ARGUMENTS));
    }

    private void parseHintLit() {
        consume();
        consume();
        StringBuilder hint = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '"') {
            hint.append(consume());
        }
        if (peekOpt().isPresent()) {
            consume();
            tokens.add(new Token("H\"" + hint + "\"", hint.toString(), TokenType.HINT_LIT));
            return;
        }
        tokens.add(new Token("H\"" + hint, hint.toString(), TokenType.TAG_LIT));
    }

    private void parseTagLit() {
        consume();
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '"') {
            buf.append(consume());
        }
        if (peekOpt().isPresent()) {
            consume();
            tokens.add(new Token("T\"" + buf.toString() + "\"", buf.toString(), TokenType.TAG_LIT));
            return;
        }
        tokens.add(new Token("T\"" + buf.toString(), buf.toString(), TokenType.TAG_LIT));
    }

    private void parseComment() {
        StringBuilder comment = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '\n') {
            comment.append(consume());
        }
        if (highlighting) {
            tokens.add(new Token(comment.toString(), TokenType.COMMENT));
        }
    }

    private void parseComponentLit() {
        consume();
        consume();
        StringBuilder content = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '"') {
            content.append(consume());
        }
        String string = content.toString();
        if (peekOpt().isEmpty()) {
            tokens.add(new Token("$\"" + string, string, TokenType.TEXT_LIT));
            return;
        }
        consume();
        tokens.add(new Token("$\"" + string + "\"", string, TokenType.TEXT_LIT));
    }

    private boolean checkAndAdd(char ch, TokenType type) {
        if (peekOpt().isEmpty()) {
            return false;
        }
        if (peek() == ch) {
            if (peek() == ' ') {
                if (!highlighting) {
                    consume();
                    return true;
                }
            }
            tokens.add(new Token(consume(), type));
            if (type == TokenType.OPEN_PAREN) {
                hasParsedBracketOpen = true;
            }
            return true;
        }
        return false;
    }

    private String parseFromUntil(char start, char end) {
        consume(); // consume start
        return parseUntil(end, true);
    }

    private String parseUntil(char end, boolean include) {
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peekOpt().get() != end) {
            buf.append(consume());
        }
        if (include) {
            consume(); // consume end
        }
        return buf.toString();
    }

    private void parseStringLit() {
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '"') {
            buf.append(consume());
        }
        String string = buf.toString();
        if (peekOpt().isEmpty()) {
            tokens.add(new Token("\"" + string, string, TokenType.STRING_LIT));
            return;
        }
        consume();
        tokens.add(new Token("\"" + string + "\"", string, TokenType.STRING_LIT));
    }

    private void parseNumber() {
        StringBuilder buf = new StringBuilder();
        int hasDigited = 0;
        while (peekOpt().isPresent() && (Character.isDigit(peek()) || peek() == '.')) {
            if (peek() == '.') {
                hasDigited++;
            }
            buf.append(consume());
        }

        String string = buf.toString();
        if (hasDigited > 1) {
            tokens.add(new Token(string, TokenType.PLAIN));
            return;
        }
        tokens.add(new Token(string, TokenType.NUMBER_LIT));
    }

    private void parseActionEncapsulated() {
        consume(); // Remove beginning " ' "
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '\'') {
            buf.append(consume());
        }
        if (peekOpt().isEmpty()) {
            tokens.add(new Token("'" + buf.toString(), buf.toString(), TokenType.ACTION));
            return;
        }
        consume(); // Remove ending " ' "


        tokens.add(new Token("'" + buf.toString() + "'", buf.toString(), TokenType.ACTION));
    }

    private void parseTarget() {
        consume();
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != '>') {
            buf.append(consume());
        }
        if (peekOpt().isEmpty()) {
            tokens.add(new Token("<" + buf.toString(), buf.toString(), TokenType.TARGET));
            return;
        }
        consume(); // Remove ending " ' "


        tokens.add(new Token("<" + buf.toString() + ">", buf.toString(), TokenType.TARGET));
    }

    private void parseVariable() {
        if (peek() == '[') {
            consume();
            StringBuilder buf = new StringBuilder();
            while (peekOpt().isPresent() && peek() != ']') {
                buf.append(consume());
            }
            String varName = buf.toString();
            if (peekOpt().isPresent()) {
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
        while (peekOpt().isPresent() && peek() != ')' && peek() != ' ') {
            buf.append(consume());
        }
        String varName = buf.toString();
        String realName = varName;
        if (!varName.endsWith("@s") && !varName.endsWith("@g") && !varName.endsWith("@i")) {
            realName = realName + "@li";
        }
        tokens.add(new Token(varName, realName, TokenType.VARIABLE));
    }

    private boolean phrase(String phrase) {
        for (int i = 0; i < phrase.length(); i++) {
            if (peekOpt(i).isEmpty() || peek(i) != phrase.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int offset) {
        return text.charAt(index + offset);
    }

    private Optional<Character> peekOpt() {
        return peekOpt(0);
    }

    private Optional<Character> peekOpt(int offset) {
        if (index + offset >= text.length()) {
            return Optional.empty();
        }
        return Optional.of(text.charAt(index + offset));
    }

    private char consume() {
        if (peekOpt().isPresent()) {
            return text.charAt(index++);
        }
        return '☺';
    }

    private String getVariablePostfix() {
        String postfix = "li";
        if (peekOpt(2).isPresent()) {
            if (peek() == '@') {
                consume();
                switch (peek()) {
                    case 's':
                        postfix = "s";
                        break;
                    case 'l':
                        postfix = "l";
                        break;
                    case 'g':
                        postfix = "g";
                        break;
                }
            }
        }
        return postfix;
    }*/
}
