package org.zbinfinn.wecode.template_editor.refactor;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.PacketHelper;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.action_dump.action.DumpAction;
import org.zbinfinn.wecode.action_dump.action.DumpActionTag;
import org.zbinfinn.wecode.action_dump.sound.DumpSound;
import org.zbinfinn.wecode.action_dump.sound.DumpSoundVariant;
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

    public record Suggestions(Optional<Text> title, List<Suggestion> list, String toBeReplaced, int displayOffset) {
        public enum Position {
            START_OF_TOKEN,
            CURSOR
        }

        private Suggestions(Optional<Text> title, List<Suggestion> suggestions, String toBeReplaced) {
            this(title, suggestions, toBeReplaced, WeCode.MC.textRenderer.getWidth(toBeReplaced));
        }

        public Suggestions(Text title, List<Suggestion> suggestions, String toBeReplaced) {
            this(Optional.of(title), suggestions, toBeReplaced);
        }

        public Suggestions(List<Suggestion> suggestions, String toBeReplaced) {
            this(Optional.empty(), suggestions, toBeReplaced);
        }

        public Suggestions(Text title) {
            this(Optional.ofNullable(title), List.of(), "");
        }

        public Suggestions() {
            this(Optional.empty(), List.of(), "");
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

        if (token.type == TokenType.SOUND_LIT) {
            return handleSoundLiteral();
        }

        if (tokenPosition.indexInTokenText() != token.lengthWithoutSuffix()) {
            return new Suggestions();
        }

        return switch (token.type) {
            case ACTION -> handleActionToken();
            case TAG_LIT -> handleTagLiteral();
            case HINT_LIT -> new Suggestions(
                List.of(
                    new Suggestion(suggestionify("function"), "function")
                ),
                token.value
            );
            default -> new Suggestions();
        };
    }

    public Suggestions handleSoundLiteral() {
        StringBuilder nameBuilder = new StringBuilder();
        boolean foundStart = false;
        for (int i = 0; i < token.text.length(); i++) {
            if (foundStart) {
                if (token.text.charAt(i) == '\'') {
                    break;
                }
                nameBuilder.append(token.text.charAt(i));
                continue;
            }
            if (token.text.charAt(i) == '\'') {
                foundStart = true;
            }
        }

        System.out.println("NAME: " + nameBuilder);

        int spaces = 0;
        boolean currentlyInSound = false;
        for (int i = 0; i < tokenPosition.indexInTokenText(); i++) {
            if (token.text.charAt(i) == '\'') {
                currentlyInSound = !currentlyInSound;
            }
            if (token.text.charAt(i) == ' ' && !currentlyInSound) {
                spaces++;
            }
        }

        return switch (spaces) {
            case 0 -> {
                String name = nameBuilder.toString();
                int endOfNameIndex = name.length() + 3; // S"'
                if (endOfNameIndex != tokenPosition.indexInTokenText()) {
                    yield new Suggestions();
                }

                List<Suggestion> suggestions = new ArrayList<>();

                var sounds = WeCode.ACTION_DUMP.sounds.sounds;
                var startingWithStream = sounds
                    .stream()
                    .filter(sound -> sound.name().startsWith(name));
                var containsStream = sounds
                    .stream()
                    .filter(sound -> sound.name().contains(name))
                    .filter(sound -> !sound.name().startsWith(name));

                suggestions.addAll(
                    startingWithStream.map(
                        sound -> suggestionifySound(sound, name)
                    ).toList()
                );
                suggestions.addAll(
                    containsStream.map(
                        sound -> suggestionifySound(sound, name)
                    ).toList()
                );

                yield new Suggestions(suggestions, name);
            }
            case 1 ->
                new Suggestions(Text.literal("<pitch>").withColor(TEColor.SUGGESTION_NOTE_GRAY.value()), List.of(), "");
            case 2 ->
                new Suggestions(Text.literal("<volume>").withColor(TEColor.SUGGESTION_NOTE_GRAY.value()), List.of(), "");
            case 3 -> {
                String name = nameBuilder.toString();
                if (!WeCode.ACTION_DUMP.sounds.soundsMap.containsKey(name)) {
                    yield new Suggestions();
                }

                int spaceCounter = 0;
                boolean opened = false;
                StringBuilder variantBuilder = new StringBuilder();
                for (int i = 0; i < token.value.length(); i++) {
                    if (spaceCounter >= 3) {
                        variantBuilder.append(token.value.charAt(i));
                    }
                    if (token.value.charAt(i) == '\'') {
                        opened = !opened;
                    }
                    if (!opened && token.value.charAt(i) == ' ') {
                        spaceCounter++;
                    }
                }

                String variantName = variantBuilder.toString();

                DumpSound sound = WeCode.ACTION_DUMP.sounds.soundsMap.get(name);
                List<DumpSoundVariant> variants = sound.variants();

                if (variants.isEmpty()) {
                    yield new Suggestions();
                }

                yield new Suggestions(
                    variants
                        .stream()
                        .filter(variant -> variant.name().contains(variantName))
                        .map(variant ->
                                 new Suggestion(suggestionify(variant.name(), variantName), variant.id()))
                        .toList(),
                    variantName
                );
            }
            default -> new Suggestions();
        };
    }

    private Suggestion suggestionifySound(DumpSound sound, String search) {
        return new Suggestion(
            suggestionify(sound.name(), search),
            sound.name()
        );
    }

    private Suggestions handleTagLiteral() {
        int countTags = 0;
        int index = tokenPosition.tokenIndex() - 1;
        String action = "";
        String actionType = "";
        while (index >= 0) {
            Token t = tokens.get(index);
            switch (t.type) {
                case TAG_LIT -> countTags++;
                case ACTION -> action = t.value;
                case ACTION_TYPE -> {
                    System.out.println("Action Type Found: " + t.value);
                    actionType = t.value;
                    index = 0;
                }
            }
            index--;
        }

        if (action.isEmpty()) {
            System.out.println("Action is empty");
            return new Suggestions();
        }

        if (actionType.isEmpty()) {
            var actions = WeCode.ACTION_DUMP.actions.getGroups();
            actionType = "PA";
            for (Map.Entry<String, Set<String>> entry : actions.entrySet()) {
                System.out.println("Set - " + entry.getKey() + ": " + entry.getValue());
                if (entry.getValue().contains(action)) {
                    actionType = TedConstants.ACTION_SPECIFIERS.inverse().get(entry.getKey());
                    break;
                }
            }
        } else {
            System.out.println(actionType);
        }

        var groupMaps = WeCode.ACTION_DUMP.actions.getGroupsMaps();
        var groupName = TedConstants.ACTION_SPECIFIERS.get(actionType);
        var groupMap = groupMaps.get(groupName);

        System.out.println("Group: " + groupName);

        DumpAction dumpAction = groupMap.get(action);
        System.out.println(groupMap);
        if (dumpAction == null) {
            System.out.println("Dumpaction is null");
            return new Suggestions();
        }
        if (countTags >= dumpAction.tags().size()) {
            System.out.println("Count Tags too high");
            return new Suggestions();
        }

        DumpActionTag tag = dumpAction.tags().get(countTags);
        var title = Text.literal(tag.name()).withColor(TEColor.VARIABLE.value());
        List<Suggestion> suggestions = new ArrayList<>();
        for (var option : tag.options()) {
            if (option.name().startsWith(token.value)) {
                suggestions.add(
                    new Suggestion(suggestionify(option.name(), token.value), option.name())
                );
            }
        }

        System.out.println("Suggested");
        return new Suggestions(
            Text.literal(tag.name()).withColor(TEColor.VARIABLE.value()),
            suggestions,
            token.value
        );
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
                .toList(),
            token.value
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

    private MutableText suggestionify(String suggestion, String search) {
        if (search.isEmpty()) {
            return Text.literal(suggestion).withColor(TEColor.SUGGESTION_TEXT.value());
        }
        String[] split = suggestion.split(Pattern.quote(search));
        return switch (split.length) {
            case 0 -> Text.literal(search).withColor(TEColor.SUGGESTION_HIGHLIGHT.value());
            case 1 -> {
                if (!suggestion.startsWith(search)) {
                    yield Text
                        .literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value())
                        .append(Text.literal(search).withColor(TEColor.SUGGESTION_HIGHLIGHT.value()));
                } else {
                    yield Text.literal(search).withColor(TEColor.SUGGESTION_HIGHLIGHT.value())
                        .append(Text.literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value()));
                }
            }
            default -> Text
                .literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value())
                .append(Text.literal(search).withColor(TEColor.SUGGESTION_HIGHLIGHT.value()))
                .append(Text.literal(split[1]).withColor(TEColor.SUGGESTION_TEXT.value()));
        };
    }

    private MutableText suggestionify(String suggestion) {
        return suggestionify(suggestion, token.value);
    }
}
