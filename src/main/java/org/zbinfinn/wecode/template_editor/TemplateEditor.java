package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.template_editor.token.*;
import org.zbinfinn.wecode.util.LerpUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TemplateEditor extends EditBox implements Widget, Drawable, Element {
    private List<List<Token>> tokens = new ArrayList<>();
    private int x, y;
    private int width, height;
    private boolean focused;

    private static final int LEFT_PADDING = 5;

    private boolean growing = false;
    private double cursorOpacity = 1;

    public TemplateEditor(int x, int y, int width, int height) {
        super(Flint.getClient().textRenderer, 100000);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }



    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    private void updateTokens() {
        tokens = new ArrayList<>();
        for (String text : getText().split("\n")) {
            Tokenizer tokenizer = new Tokenizer(text);
            var tokenized = tokenizer.tokenize();
            tokens.add(tokenized);
        }
    }

    public ItemStack toItem() {
        return new ItemStack(Items.STONE);
    }

    public void clear() {
        setText("");
        updateTokens();
    }

    public void setText(String newText) {
        super.setText(newText);
        updateTokens();
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        draw.fill(x, y, x + width, y + height, 0xAA000000);

        TextRenderer tRend = WeCode.MC.textRenderer;
        int lineY = 4;
        for (List<Token> tokArr : tokens) {
            int lineX = LEFT_PADDING;
            for (Token token : tokArr) {
                TokenType type = token.type;
                int color = TEColor.fromType(type);
                String text = token.text;
                draw.drawTextWithShadow(tRend, text, x + lineX, x + lineY, color);
                lineX += tRend.getWidth(text);
            }

            lineY += tRend.fontHeight;
        }

        final int BASE_X = LEFT_PADDING - 1;

        int cursorX = BASE_X;
        int cursorY = 0;

        final var growth = 0.05;
        if (growing) {
            cursorOpacity += growth * delta;
            if (cursorOpacity >= 1) {
                cursorOpacity = 1;
                growing = false;
            }
        } else {
            cursorOpacity -= growth * delta;
            if (cursorOpacity <= 0) {
                cursorOpacity = 0;
                growing = true;
            }
        }

        for (int i = 0; i < getCursor(); i++) {
            char c = getText().toCharArray()[i];
            if (c == '\n') {
                cursorX = BASE_X;
                cursorY += WeCode.MC.textRenderer.fontHeight;
                continue;
            }
            cursorX += WeCode.MC.textRenderer.getWidth(String.valueOf(c));
        }

        final int fontHeight = WeCode.MC.textRenderer.fontHeight;
        final int fontOffset = fontHeight/2 - 1;
        draw.fill(x + cursorX, y + cursorY + fontOffset, x + cursorX + 1, y + cursorY + fontHeight + fontOffset, calcCursorColor());
        //draw.drawVerticalLine(x + cursorX, y + cursorY + fontHeight/2 , y + cursorY + WeCode.MC.textRenderer.fontHeight, 0xFFFFFFFF);
        //draw.drawVerticalLine(cursorX, x + cursorY + WeCode.MC.textRenderer.fontHeight/4, y + cursorY + WeCode.MC.textRenderer.fontHeight + WeCode.MC.textRenderer.fontHeight/2 , 0xFFFFFFFF);
    }

    private int calcCursorColor() {
        return new Color(200, 200, 200, (int) LerpUtil.easeInOutSin(0.3*255, 255, cursorOpacity)).getRGB();
    }

    @Override
    public int getNavigationOrder() {
        return 1;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(x, y, width, height);
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        // Nothing?
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = handleSpecialKey(keyCode);
        updateTokens();
        return handled;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        setSelecting(Screen.hasShiftDown());
        replaceSelection(String.valueOf(chr));
        updateTokens();
        return true;
    }
}
