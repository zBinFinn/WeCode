package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.PlotDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LineStarterDisplay implements Widget, Drawable, Element, Selectable {
    private final List<LineStarterSelectButton> buttons = new ArrayList<>();
    private int x, y;
    private int width, height;
    private boolean focused;
    private TextRenderer tr;
    private Action action;
    private boolean caching = false;

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    public interface Action {
        void clickedLineStarterSelector(LineStarter lineStarter);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonint) {
        int x = this.x;
        int y = this.y + 2;
        for (LineStarterSelectButton button : buttons) {
            if (button.mouseOver(x, y, width, tr.fontHeight + 4, mouseX, mouseY)) {
                action.clickedLineStarterSelector(button.getLineStarter());
                return true;
            }
            y += tr.fontHeight + 4;
        }

        return false;
    }

    public LineStarterDisplay(int x, int y, int width, int height, Action action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        tr = Flint.getClient().textRenderer;
        this.action = action;
    }

    private void cacheLineStarters() {
        if (!PlotDataManager.getLineStarters().isEmpty()) {
            buttons.clear();
            for (LineStarter lineStarter : PlotDataManager.getLineStarters()) {
                buttons.add(new LineStarterSelectButton(
                    lineStarter
                ));
            }
            caching = false;
        }
    }


    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        if (caching) {
            cacheLineStarters();
        }

        draw.fill(x, y, x + width, y + height, 0x88000000);

        int x = this.x;
        int y = this.y + 2;
        for (LineStarterSelectButton button : buttons) {
            button.render(draw, x, y, width, tr.fontHeight + 4, mouseX, mouseY);
            y += tr.fontHeight + 4;
        }
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
        return Widget.super.getNavigationFocus();
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public int getNavigationOrder() {
        return Element.super.getNavigationOrder();
    }

    @Override
    public SelectionType getType() {
        return SelectionType.FOCUSED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
