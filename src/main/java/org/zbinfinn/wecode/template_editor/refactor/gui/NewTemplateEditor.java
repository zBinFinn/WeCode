package org.zbinfinn.wecode.template_editor.refactor.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyCodes;
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

import java.util.ArrayList;
import java.util.List;

public class NewTemplateEditor extends EditTextWidget implements Renderable {
    private static final int LEFT_PADDING = 5;
    private static final int TOP_PADDING = 5;

    private static final int LINE_SPACING = 0;

    private final Suggester suggester = new Suggester();
    private final TextRenderer tr;
    private final LineStarter lineStarter;

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
    }



    @Override
    public void render(DrawContext draw) {
        draw.fill(pos().getX(), pos().getY(), pos().getRightX(), pos().getBottomY(), 0xAA000000);

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

        TedUtil.Pos pos = TedUtil.getCursorPositionOnScreen(getText(), getCursor());
        draw.fill(pos.x(), pos.y(), pos.x() + 1, pos.y() + tr.fontHeight, 0xFFFF8888);

        {
            suggestions = suggester.suggest(tokens, getCursor());

            int x = pos.x();
            int y = pos.y() + getTotalLineSpacing();
            int originalY = y;

            int biggestX = 0;
            for (int i = 0; i < suggestions.list().size() && i < TedConstants.SUGGESTIONS; i++) {
                var suggestion = suggestions.list().get(i);

                if (tr.getWidth(suggestion.text()) > biggestX) {
                    biggestX = tr.getWidth(suggestion.text());
                }

                draw.drawText(tr, suggestion.text(), x, y, 0xFFFFFF, false);

                y += getTotalLineSpacing();

                draw.getMatrices().push();
                draw.getMatrices().translate(0, 0, -1);
                draw.fill(x, originalY, biggestX, y, 0x00000000);
                draw.getMatrices().pop();
            }
        }
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
                for (int i = 0; i < token.value.length(); i++) {
                    delete(-1);
                }
                replaceSelection(suggestion.value());
            }
            updateTokens();
            return true;
        }

        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        updateTokens();
        return result;
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        updateTokens();
    }
}
