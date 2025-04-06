package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import org.zbinfinn.wecode.plotdata.LineStarter;

public class LineStarterSelectButton {
    private LineStarter lineStarter;
    private TextRenderer tr;
    private int yPadding;
    public LineStarterSelectButton(LineStarter lineStarter) {
        this.lineStarter = lineStarter;
        tr = Flint.getClient().textRenderer;
    }
    public String getValue() {
        return lineStarter.getName();
    }
    public void render(DrawContext draw, int x, int y, int width, int height, int mouseX, int mouseY) {
        int color = 0xAA000000;
        if (mouseOver(x, y, width, height, mouseX, mouseY)) {
            color = 0xAA444444;
        }

        draw.fill(x, y, x + width, y + height, color);
        String displayName = lineStarter.getName();
        if (tr.getWidth(displayName) > width) {
            while (tr.getWidth(displayName) > (width - tr.getWidth(".."))) {
                displayName = displayName.substring(0, displayName.length() - 1);
            }
            displayName = displayName + "..";
        }
        draw.drawCenteredTextWithShadow(tr, displayName, x + width / 2, y + (height-tr.fontHeight)/2, 0xFFFFFF);
    }

    public boolean mouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
        return new ScreenRect(x, y, width, height).contains((int) mouseX, (int) mouseY);
    }

    public LineStarter getLineStarter() {
        return lineStarter;
    }
}
