package org.zbinfinn.wecode.template_editor.refactor;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.action_dump.DumpAction;
import org.zbinfinn.wecode.template_editor.TEColor;
import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;

import java.util.*;
import java.util.regex.Pattern;

public class Suggester {
    private List<Token> tokens;
    private int cursorIndex;
    private TedUtil.CursorTokenPosition tokenPosition;
    private Token token;

    public Suggester() {
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void setCursorIndex(int cursorIndex) {
        this.cursorIndex = cursorIndex;
    }

    public record Suggestions(Optional<Text> title, List<Suggestion> list) {
        public Suggestions(List<Suggestion> suggestions) {
            this(Optional.empty(), suggestions);
        }

        public Suggestions() {
            this(Optional.empty(), List.of());
        }
    }

    public record Suggestion(Text text, String value) {
    }

    public Suggestions suggest(List<Token> tokens, int index) {
        this.tokens = tokens;
        this.cursorIndex = index;
        return suggest();
    }

    public Suggestions suggest() {
        tokenPosition = TedUtil.getTokenIndexFromCursor(tokens, cursorIndex);
        token = tokens.get(tokenPosition.tokenIndex());
        if (tokenPosition.indexInTokenText() != token.lengthWithoutSuffix()) {
            return new Suggestions();
        }

        return switch (token.type) {
            case ACTION -> handleActionToken();
            default -> new Suggestions();
        };
    }

    private Suggestions handleActionToken() {
        Set<DumpAction> actions = WeCode.ACTION_DUMP.actions.getActions();

        var startsWithStream = actions
            .stream()
            .filter(action -> action.name().startsWith(token.value));

        var containsStream = actions
            .stream()
            .filter(action -> action.name().contains(token.value))
            .filter(action -> !action.name().startsWith(token.value));

        if (tokenPosition.tokenIndex() - 2 > 0 && tokens.get(tokenPosition.tokenIndex() - 2).type == TokenType.ACTION_TYPE) {
            String specifier = tokens.get(tokenPosition.tokenIndex() - 2).value;
            String filter = TedConstants.ACTION_SPECIFIERS.get(specifier);

            startsWithStream = startsWithStream.filter(token -> token.block().equals(filter));
            containsStream = containsStream.filter(token -> token.block().equals(filter));
        }

        List<DumpAction> tempSuggestions = new ArrayList<>();
        tempSuggestions.addAll(
            startsWithStream.sorted().toList()
        );
        tempSuggestions.addAll(
            containsStream.sorted().toList()
        );

        Set<String> entries = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (var suggestion : tempSuggestions) {
            if (!entries.contains(suggestion.name())) {
                entries.add(suggestion.name());
            } else {
                duplicates.add(suggestion.name());
            }
        }

        return new Suggestions(
            tempSuggestions
                .stream()
                .map(dumpAction -> new Suggestion(suggestionifyAction(dumpAction, duplicates.contains(dumpAction.name())), dumpAction.name()))
                .toList()
        );
    }

    private Text suggestionifyAction(DumpAction action, boolean duplicate) {
        if (!duplicate) {
            return suggestionify(action.name());
        }
        return suggestionify(action.name())
            .append(Text.literal(" " + TedConstants.ACTION_SPECIFIERS.inverse().get(action.block()))
                        .withColor(TEColor.SUGGESTION_EXTRA.value()));
    }

    private MutableText suggestionify(String suggestion) {
        if (token.value.isEmpty()) {
            return Text.literal(suggestion).withColor(TEColor.SUGGESTION_TEXT.value());
        }
        String[] split = suggestion.split(Pattern.quote(token.value));
        return switch (split.length) {
            case 0 -> Text.literal(token.value).withColor(TEColor.SUGGESTION_HIGHLIGHT.value());
            case 1 -> {
                if (!suggestion.startsWith(token.value)) {
                    yield Text
                        .literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value())
                        .append(Text.literal(token.value).withColor(TEColor.SUGGESTION_HIGHLIGHT.value()));
                } else {
                    yield Text.literal(token.value).withColor(TEColor.SUGGESTION_HIGHLIGHT.value())
                        .append(Text.literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value()));
                }
            }
            default -> Text
                .literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value())
                .append(Text.literal(token.value).withColor(TEColor.SUGGESTION_HIGHLIGHT.value()))
                .append(Text.literal(split[1]).withColor(TEColor.SUGGESTION_TEXT.value()));
        };
    }
}
