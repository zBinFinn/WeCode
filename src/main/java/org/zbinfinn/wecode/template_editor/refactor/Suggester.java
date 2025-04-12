package org.zbinfinn.wecode.template_editor.refactor;

import org.zbinfinn.wecode.template_editor.token.Token;

import java.util.List;

public class Suggester {
    private List<Token> tokens;
    private int index;

    public Suggester(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public record Suggestion(String text) {}

    public List<Suggestion> suggest(List<Token> tokens, int index) {
        this.tokens = tokens;
        this.index = index;
        return suggest();
    }
    public List<Suggestion> suggest() {
        return List.of(
            new Suggestion("Example Suggestion")
        );
    }
}
