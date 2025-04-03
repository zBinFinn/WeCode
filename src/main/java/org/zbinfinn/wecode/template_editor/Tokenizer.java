package org.zbinfinn.wecode.template_editor;

import com.google.common.collect.BiMap;
import org.spongepowered.include.com.google.common.collect.HashBiMap;
import org.zbinfinn.wecode.template_editor.token.*;

import java.util.*;

public class Tokenizer {
    private static final Map<Character, TokenType> CHECK_AND_ADD_MAP;
    public static final HashBiMap<String, String> ACTION_SPECIFIERS;

    static {
        CHECK_AND_ADD_MAP = new HashMap<>();
        CHECK_AND_ADD_MAP.put('(', TokenType.OPEN_PAREN);
        CHECK_AND_ADD_MAP.put(')', TokenType.CLOSE_PAREN);
        CHECK_AND_ADD_MAP.put('{', TokenType.OPEN_CURLY);
        CHECK_AND_ADD_MAP.put('}', TokenType.CLOSE_CURLY);
        CHECK_AND_ADD_MAP.put(' ', TokenType.SPACE);

        ACTION_SPECIFIERS = HashBiMap.create();
        ACTION_SPECIFIERS.put("PE", "PLAYEREVENT"); // Player Event
        ACTION_SPECIFIERS.put("PA", "PLAYERACTION"); // Player Action
        ACTION_SPECIFIERS.put("IP", "IFPLAYER"); // If Player

        ACTION_SPECIFIERS.put("EE", "ENTITYEVENT"); // Entity Event
        ACTION_SPECIFIERS.put("EA", "ENTITYACTION"); // Entity Action
        ACTION_SPECIFIERS.put("IE", "IFENTITY"); // If Entity

        ACTION_SPECIFIERS.put("SV", "SETVARIABLE"); // Set Variable
        ACTION_SPECIFIERS.put("IV", "IFVARIABLE"); // If Variable

        ACTION_SPECIFIERS.put("GA", "GAMEACTION"); // Game Action
        ACTION_SPECIFIERS.put("IG", "IFGAME"); // If Game

        ACTION_SPECIFIERS.put("SO", "SELECTOBJECT"); // Select Object

        // Else Doesn't Have One it's just "Else"

        ACTION_SPECIFIERS.put("FN", "FUNCTION"); // Function (Always needs to be specified)
        ACTION_SPECIFIERS.put("CF", "CALLFUNCTION"); // Call Function (Always needs to be specified)

        ACTION_SPECIFIERS.put("PC", "PROCESS"); // Process (Always needs to be specified)
        ACTION_SPECIFIERS.put("SP", "STARTPROCESS"); // Start Process (Always needs to be specified)

        ACTION_SPECIFIERS.put("CT", "CONTROL"); // Control

        ACTION_SPECIFIERS.put("RP", "REPEAT"); // Repeat
    }

    private final String text;
    private int index = 0;
    private final List<Token> tokens = new ArrayList<>();
    private boolean hasParsedBracketOpen = false;

    public Tokenizer(String text) {
        this.text = text;
    }

    public List<Token> tokenize() {

        while (peekOpt().isPresent()) {
            boolean shouldContinue = false;
            for (Map.Entry<Character, TokenType> entry : CHECK_AND_ADD_MAP.entrySet()) {
                if (checkAndAdd(entry.getKey(), entry.getValue())) {
                    shouldContinue = true;
                    break;
                }
            }
            if (shouldContinue) {
                continue;
            }
            if (peek() == '/' && peekOpt(1).isPresent() && peek(1) == '/') {
                parseComment();
            } else if (Character.isDigit(peek())) {
                parseNumber();
            } else if (peek() == '$' && peekOpt(1).isPresent() && peek(1) == '"') {
                parseComponentLit();
            } else if (Character.isAlphabetic(peek())) {
                if (hasParsedBracketOpen) {
                    parseVariable();
                } else {
                    parseAction();
                }
            } else if (peek() == '"') {
                parseStringLit();
            } else if (peek() == '[') {
                parseVariable();
            } else if (peek() == '\'') {
                parseActionEncapsulated();
            } else if (peek() == '<') {
                parseTarget();
            } else {
                tokens.add(new Token(consume(), TokenType.PLAIN));
            }
        }

        return tokens;
    }

    private void parseComment() {
        StringBuilder comment = new StringBuilder();
        while (peekOpt().isPresent()) {
            comment.append(consume());
        }
        tokens.add(new Token(comment.toString(), TokenType.COMMENT));
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
            tokens.add(new Token("$\"" + string, string, TokenType.COMPONENT_LIT));
            return;
        }
        consume();
        tokens.add(new Token("$\"" + string + "\"", string, TokenType.COMPONENT_LIT));
    }

    private boolean checkAndAdd(char ch, TokenType type) {
        if (peekOpt().isEmpty()) {
            return false;
        }
        if (peek() == ch) {
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

    private void parseAction() {
        StringBuilder buf = new StringBuilder();
        do {
            buf.append(consume());
        } while (peekOpt().isPresent() && (Character.isAlphabetic(peek()) || Character.isDigit(peek())));

        String string = buf.toString();
        if (ACTION_SPECIFIERS.containsKey(string)) {
            tokens.add(new Token(string, TokenType.ACTION_TYPE));
            return;
        }

        tokens.add(new Token(string, TokenType.ACTION));
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
        tokens.add(new Token(string, TokenType.INTEGER_LIT));
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
            String varName = parseFromUntil('[', ']');
            tokens.add(new Token("[" + varName + "]", varName, TokenType.VARIABLE));
            return;
        }
        StringBuilder buf = new StringBuilder();
        while (peekOpt().isPresent() && peek() != ')' && peek() != ' ') {
            buf.append(consume());
        }
        tokens.add(new Token(buf.toString(), TokenType.VARIABLE));
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
}
