package org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer;

import org.zbinfinn.wecode.helpers.MessageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[+\\-*/()]"
    );

    public static Optional<List<Token>> tokenize(String input) {
        input = input.replaceAll("\\s+", "");
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() != lastEnd) {
                return Optional.empty();
            }

            String match = matcher.group();
            lastEnd = matcher.end();

            if (match.matches("\\d+") || match.matches("\\d+\\.\\d+")) {
                tokens.add(new Token(TokenType.NUMBER, match));
            } else if (match.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add(new Token(TokenType.VARIABLE, match));
            } else if ("+-*/()".contains(match)) {
                TokenType type = switch (match) {
                    case "(" -> TokenType.LEFT_PAREN;
                    case ")" -> TokenType.RIGHT_PAREN;
                    default -> TokenType.OPERATOR;
                };
                tokens.add(new Token(type, match));
            }
        }

        // Check if the entire string was matched
        if (lastEnd != input.length()) {
            return Optional.empty(); // Unrecognized character(s) remain
        }

        return Optional.of(tokens);
    }
}
