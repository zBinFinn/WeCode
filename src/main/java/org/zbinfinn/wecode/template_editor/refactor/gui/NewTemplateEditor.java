package org.zbinfinn.wecode.template_editor.refactor.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.template_editor.refactor.Suggester;
import org.zbinfinn.wecode.template_editor.refactor.TedConstants;
import org.zbinfinn.wecode.template_editor.refactor.TedUtil;
import org.zbinfinn.wecode.template_editor.refactor.Tokenizer;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.FixedPositioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.Renderable;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.EditTextWidget;
import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;
import org.zbinfinn.wecode.util.LerpUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NewTemplateEditor extends EditTextWidget implements Renderable {
    private static final int LEFT_PADDING = 5;
    private static final int TOP_PADDING = 5;

    private static final int LINE_SPACING = 0;

    private final Suggester suggester = new Suggester();
    private final TextRenderer tr;
    private final LineStarter lineStarter;

    private double cursorLerp = 0;
    private boolean cursorGrowing = true;

    private Suggester.Suggestions suggestions = new Suggester.Suggestions();
    private int suggestionIndex = 0;
    private List<Token> tokens = new ArrayList<>();

    public LineStarter getLineStarter() {
        return lineStarter;
    }

    public NewTemplateEditor(LineStarter lineStarter, String content, Positioning positioning) {
        super(WeCode.MC.textRenderer, 9999, positioning);
        tr = WeCode.MC.textRenderer;
        this.lineStarter = lineStarter;
        setText(content);
        updateTokens();
    }

    public NewTemplateEditor(LineStarter line, String content) {
        this(line, content, new FixedPositioning(TedConstants.Dimensions.editorX(), TedConstants.Dimensions.editorY(), TedConstants.Dimensions.editorWidth(), TedConstants.Dimensions.editorHeight()));
    }

    public NewTemplateEditor(LineStarter lineStarter) {
        this(lineStarter, "");
    }

    public String getName() {
        return lineStarter.getName();
    }

    private void updateTokens() {
        Tokenizer tokenizer = new Tokenizer(getText());
        tokens = tokenizer.tokenize(false);
        updateSuggestions();
    }


    @Override
    public void render(DrawContext draw, float delta) {
        draw.fill(pos().getX(), pos().getY(), pos().getRightX(), pos().getBottomY(), 0xAA000000);

        /*
            Render Text
         */
        {
            int x = getTextX();
            int y = getTextY();

            for (var token : tokens) {
                if (token.type == TokenType.EOL) {
                    y += getTotalLineSpacing();
                    x = getTextX();
                    continue;
                }
                draw.drawText(tr, token.toText(), x, y, 0xFFFFFF, false);
                x += tr.getWidth(token.toText());
            }
        }

        /*
            Render Cursor
         */
        cursorLerp += delta * 0.15 * (cursorGrowing ? 1 : -1);
        if (cursorLerp >= 1) {
            cursorGrowing = false;
            cursorLerp = 1;
        }
        if (cursorLerp <= 0) {
            cursorGrowing = true;
            cursorLerp = 0;
        }
        TedUtil.Pos pos = TedUtil.getCursorPositionOnScreen(getText(), getCursor());
        int color = new Color(200, 200, 200, (int) LerpUtil.easeInOutSin(0.3 * 255, 255, cursorLerp)).getRGB();
        draw.fill(pos.x(), pos.y(), pos.x() + 1, pos.y() + tr.fontHeight, color);

        /*
            Render Suggestions
         */
        {
            draw.getMatrices().push();
            draw.getMatrices().translate(0, 0, 5);
            Token token = tokens.get(TedUtil.getTokenIndexFromCursor(tokens, getCursor()).tokenIndex());
            int x = pos.x() - suggestions.displayOffset();
            int y = pos.y() + getSuggestionLineSpacing();
            int biggestX = 0;

            if (suggestions.title().isPresent()) {
                draw.drawTextWithShadow(tr, suggestions.title().get(), x, y, 0xFFFFFF);
                y += getSuggestionLineSpacing();
                biggestX = x + tr.getWidth(suggestions.title().get());
            }

            int originalY = y;
            for (int i = 0; i < suggestions.list().size() && i < TedConstants.SUGGESTIONS; i++) {
                var suggestion = suggestions.list().get(i);

                Text text = suggestion.text();

                if (tr.getWidth(suggestion.text()) + x > biggestX) {
                    biggestX = tr.getWidth(suggestion.text()) + x;
                }

                draw.drawText(tr, text, x, y + 1, 0xFFFFFF, false);
                y += getSuggestionLineSpacing();
            }

            int newY = originalY;
            draw.getMatrices().push();
            draw.getMatrices().translate(0, 0, -1);
            if (suggestions.title().isPresent()) {
                draw.fill(x - 1, originalY - getSuggestionLineSpacing() - 1, biggestX + 1, originalY, 0xFF222222);
            }
            for (int i = 0; i < suggestions.list().size() && i < TedConstants.SUGGESTIONS; i++) {
                int bgColor = (i == suggestionIndex) ? 0xFF444444 : 0xFF333333;
                draw.fill(x - 1, newY, biggestX + 1, newY + getSuggestionLineSpacing() + 1, bgColor);
                newY += getSuggestionLineSpacing();
            }
            draw.getMatrices().pop();


            draw.getMatrices().pop();
        }
    }

    private int getSuggestionLineSpacing() {
        return tr.fontHeight + 2;
    }

    private int getTotalLineSpacing() {
        return tr.fontHeight + LINE_SPACING;
    }

    private int getTextX() {
        return pos().getX() + LEFT_PADDING;
    }

    private int getTextY() {
        return pos().getY() + TOP_PADDING;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            if (!suggestions.list().isEmpty()) {
                Suggester.Suggestion suggestion = suggestions.list().get(suggestionIndex);
                Token token = tokens.get(TedUtil.getTokenIndexFromCursor(tokens, getCursor()).tokenIndex());
                setSelecting(false);
                delete(-suggestions.toBeReplaced().length());
                replaceSelection(suggestion.value());
            }
            updateTokens();
            return true;
        }

        if (!suggestions.list().isEmpty()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_DOWN: {
                    if (suggestionIndex < suggestions.list().size() - 1) {
                        suggestionIndex++;
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_UP: {
                    if (suggestionIndex > 0) {
                        suggestionIndex--;
                    }
                    return true;
                }
            }
        }

        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) {
            updateTokens();
        }
        return result;
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        updateTokens();
    }

    private void updateSuggestions() {
        suggestions = suggester.suggest(tokens, getCursor());
        suggestionIndex = 0;
    }
}
