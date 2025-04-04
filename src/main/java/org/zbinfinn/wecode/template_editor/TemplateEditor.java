package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.action_dump.DumpAction;
import org.zbinfinn.wecode.template_editor.token.*;
import org.zbinfinn.wecode.util.LerpUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class TemplateEditor extends EditBox implements Widget, Drawable, Element, Selectable {
    public static final int TAB_SPACES = 3;

    private boolean visible = false;
    private String name;
    private List<List<Token>> tokens = new ArrayList<>();
    private int x, y;
    private int width, height;
    private boolean focused;
    private List<DumpAction> suggestions = new ArrayList<>();
    private Set<String> actionsWithDuplicate = new HashSet<>();

    private static final int LEFT_PADDING = 5;
    private static final int MAX_SUGGESTIONS = 15;

    private boolean growing = false;
    private double cursorOpacity = 1;

    private String searchTerm = "";

    public TemplateEditor(int x, int y, int width, int height) {
        super(Flint.getClient().textRenderer, 100000);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getBoundingBox().contains((int) mouseX, (int) mouseY);
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
            var tokenized = tokenizer.tokenize(true);
            tokens.add(tokenized);
        }

        updateSuggestions();
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
        if (!visible) {
            return;
        }

        draw.fill(x, y, x + width, y + height, 0xAA000000);

        TextRenderer tRend = WeCode.MC.textRenderer;
        int lineY = this.y - tRend.fontHeight + 4;
        for (List<Token> tokArr : tokens) {
            int lineX = this.x - 4;
            for (Token token : tokArr) {
                TokenType type = token.type;
                int color = TEColor.fromType(type);
                String text = token.text;
                draw.drawTextWithShadow(tRend, text, x + lineX, x + lineY, color);
                lineX += tRend.getWidth(text);
            }

            lineY += tRend.fontHeight;
        }

        if (focused) {
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

            Pos cursorPos = getPositionFromIndex(getCursor());

            final int fontHeight = WeCode.MC.textRenderer.fontHeight;
            final int fontOffset = fontHeight / 2 - 1;
            draw.fill(x + cursorPos.x(), y + cursorPos.y() + fontOffset, x + cursorPos.x() + 1, y + cursorPos.y() + fontHeight + fontOffset, calcCursorColor());

            renderSuggestions(draw, cursorPos);

        }
        draw.getMatrices().push();
        draw.getMatrices().translate(0, 0, 50);
        draw.drawTextWithShadow(tRend, name, 0, 0, 0xFF8888);
        draw.getMatrices().pop();
        //draw.drawVerticalLine(x + cursorX, y + cursorY + fontHeight/2 , y + cursorY + WeCode.MC.textRenderer.fontHeight, 0xFFFFFFFF);
        //draw.drawVerticalLine(cursorX, x + cursorY + WeCode.MC.textRenderer.fontHeight/4, y + cursorY + WeCode.MC.textRenderer.fontHeight + WeCode.MC.textRenderer.fontHeight/2 , 0xFFFFFFFF);
    }

    private void renderSuggestions(DrawContext draw, Pos cursorPos) {
        if (suggestions.isEmpty()) {
            return;
        }

        if (suggestions.size() == 1 && searchTerm.equals(suggestions.getFirst().name())) {
            return;
        }

        TextRenderer tr = WeCode.MC.textRenderer;

        //final int direction = (cursorPos.y > this.y + this.height / 2) ? -1 : 1;
        final int direction = 1;

        final int X_OFFSET = 10;
        final int Y_OFFSET = (direction == 1) ? (tr.fontHeight * 4) : (int) (tr.fontHeight * 3.5);

        int x = cursorPos.x() - tr.getWidth(searchTerm) + X_OFFSET; //+ X_OFFSET - tr.getWidth(searchTerm) - 2;
        int y = cursorPos.y() + Y_OFFSET;

        int leftX = x;
        int rightX = x;
        int topY = y + tr.fontHeight;


        draw.getMatrices().push();
        draw.getMatrices().translate(0, 0, 5);
        for (int i = 0; i < MAX_SUGGESTIONS && i < suggestions.size(); i++) {
            DumpAction suggestion = suggestions.get(i);
            y += direction * tr.fontHeight;
            draw.drawText(tr, suggestionify(suggestion, searchTerm), x, y, 0xFFFFFF, false);
            if (x + tr.getWidth(suggestionify(suggestion, searchTerm)) > rightX) {
                rightX = x + tr.getWidth(suggestionify(suggestion, searchTerm));
            }
        }
        draw.getMatrices().translate(0, 0, -1);

        int bottomY = y + direction * tr.fontHeight;

        if (direction == -1) {
            bottomY += tr.fontHeight;
            topY -= tr.fontHeight;
        }
        if (direction == 1) {
            draw.fill(leftX - 1, topY - 1, rightX + 1, bottomY + 1, TEColor.SUGGESTION_BACKGROUND.value());
        } else {
            draw.fill(leftX - 1, bottomY - 1, rightX + 1, topY + 1, TEColor.SUGGESTION_BACKGROUND.value());
        }
        draw.getMatrices().pop();
    }

    private Text suggestionify(DumpAction suggestion, String searchTerm) {
        String action = suggestion.name();

        if (searchTerm.isEmpty()) {
            return Text.literal(suggestion.name()).withColor(TEColor.SUGGESTION_TEXT.value());
        }

        String[] split = action.split(Pattern.quote(searchTerm));
        MutableText out = switch (split.length) {
            case 0 -> Text.literal(searchTerm).withColor(TEColor.SUGGESTION_HIGHLIGHT.value());
            case 1 -> {
                if (!suggestion.name().startsWith(searchTerm)) {
                    yield Text
                        .literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value())
                        .append(Text.literal(searchTerm).withColor(TEColor.SUGGESTION_HIGHLIGHT.value()));
                } else {
                    yield Text.literal(searchTerm).withColor(TEColor.SUGGESTION_HIGHLIGHT.value())
                        .append(Text.literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value()));
                }
            }
            default -> Text
                .literal(split[0]).withColor(TEColor.SUGGESTION_TEXT.value())
                .append(Text.literal(searchTerm).withColor(TEColor.SUGGESTION_HIGHLIGHT.value()))
                .append(Text.literal(split[1]).withColor(TEColor.SUGGESTION_TEXT.value()));
        };

        if (actionsWithDuplicate.contains(suggestion.name())) {
            out.append(
                Text.literal(" " + Tokenizer.ACTION_SPECIFIERS.inverse().get(suggestion.block()))
                    .withColor(TEColor.SUGGESTION_EXTRA.value())
            );
        }

        return out;
    }

    private Pos getPositionFromIndex(int index) {
        int x = LEFT_PADDING;
        int y = 0;

        for (int i = 0; i < getCursor(); i++) {
            char c = getText().toCharArray()[i];
            if (c == '\n') {
                x = LEFT_PADDING;
                y += WeCode.MC.textRenderer.fontHeight;
                continue;
            }
            x += WeCode.MC.textRenderer.getWidth(String.valueOf(c));
        }

        return new Pos(x, y);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private record Pos(int x, int y) {
    }

    private int calcCursorColor() {
        return new Color(200, 200, 200, (int) LerpUtil.easeInOutSin(0.3 * 255, 255, cursorOpacity)).getRGB();
    }

    @Override
    public int getNavigationOrder() {
        return 0;
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
        return getBoundingBox();
    }

    private ScreenRect getBoundingBox() {
        return new ScreenRect(x, y, width, height);
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        // Nothing?
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            for (int i = 0; i < TAB_SPACES; i++) {
                charTyped(' ', 0);
            }
            return true;
        }
        boolean handled = handleSpecialKey(keyCode);
        if (handled) {
            updateTokens();
        } else {
            updateSuggestions();
        }
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        setSelecting(Screen.hasShiftDown());
        replaceSelection(String.valueOf(chr));
        updateTokens();
        return true;
    }

    private void updateSuggestions() {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
        }
        if (actionsWithDuplicate == null) {
            actionsWithDuplicate = new HashSet<>();
        }
        actionsWithDuplicate.clear();
        suggestions.clear();

        int currentTokenIndex = getTokenIndexAtCharIndex(getCursor());

        //System.out.println("Token Index: " + currentTokenIndex);

        Token currentToken = getTokenAtTokenIndex(currentTokenIndex);

        if (currentToken == null) {
            suggestions.clear();
            return;
        }

        searchTerm = currentToken.value;

        if (currentToken.type == TokenType.ACTION) {


            Set<DumpAction> actions = WeCode.ACTION_DUMP.actions.getActions();

            var startsWithStream = actions
                .stream()
                .filter(action -> action.name().startsWith(currentToken.value));

            var containsStream = actions
                .stream()
                .filter(action -> action.name().contains(currentToken.value))
                .filter(action -> !action.name().startsWith(currentToken.value));

            // -2 to skip the space
            if (getTokenAtTokenIndex(currentTokenIndex - 2) != null && getTokenAtTokenIndex(currentTokenIndex - 2).type == TokenType.ACTION_TYPE) {
                String specifier = getTokenAtTokenIndex(currentTokenIndex - 2).value;
                String filter = Tokenizer.ACTION_SPECIFIERS.get(specifier);

                startsWithStream = startsWithStream.filter(token -> token.block().equals(filter));
                containsStream = containsStream.filter(token -> token.block().equals(filter));

                //System.out.println("Filter: " + filter);
            }

            suggestions.addAll(
                startsWithStream
                    .sorted()
                    .toList());

            suggestions.addAll(
                containsStream
                    .sorted()
                    .toList()
            );
        }

        //System.out.println("Curr: " + currentToken);
        //System.out.println("Curs: " + getCursor());
        Set<String> temp = new HashSet<>();
        for (var suggestion : suggestions) {
            if (!temp.contains(suggestion.name())) {
                temp.add(suggestion.name());
            } else {
                actionsWithDuplicate.add(suggestion.name());
            }
            //System.out.println(suggestion);
        }
    }

    private Token getTokenAtCharIndex(int index) {
        int progress = 0;
        index = getTokenIndexAtCharIndex(index);
        return getTokenAtTokenIndex(index);
    }

    private Token getTokenAtTokenIndex(int index) {
        if (index == -1) {
            return null;
        }
        int prog = 0;
        for (var line : tokens) {
            for (var token : line) {
                prog++;
                if (prog >= index) {
                    return token;
                }
            }
        }
        return null;
    }

    private int getTokenIndexAtCharIndex(int index) {
        //System.out.println("Index" + index);
        int out = 0;
        int progress = 0;
        for (List<Token> line : tokens) {
            for (Token token : line) {
                progress += token.text.length();
                out++;
                if (progress >= index) {
                    return out;
                }
            }
            progress++;
            if (progress >= index) {
                return (out == 0) ? -1 : out;
            }
        }
        return -1;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.FOCUSED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        //
    }

    @Override
    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return !this.isFocused() ? GuiNavigationPath.of(this) : null;
    }
}
