package org.zbinfinn.wecode.template_editor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;

public class TemplateTabButton extends ButtonWidget {
    private final int id;
    private boolean selected = false;
    public TemplateTabButton(int x, int y, int width, int height, Text message, PressAction onPress, int id) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.id = id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public int getId() {
        return id;
    }

    @Override
    protected void renderWidget(DrawContext draw, int mouseX, int mouseY, float delta) {
        int background = determineBackgroundColor(mouseX, mouseY);
        draw.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), background);

        TextRenderer tr = WeCode.MC.textRenderer;
        int paddingY = (this.getHeight() - tr.fontHeight) / 2;
        draw.drawCenteredTextWithShadow(tr, this.getMessage(), this.getX() + width/2, this.getY() + paddingY, 0xFFFFFF);
    }

    private int determineBackgroundColor(int mouseX, int mouseY) {
        if (selected) {
            return 0x88777777;
        }
        if (isMouseOver(mouseX, mouseY)) {
            return 0x88333333;
        }
        return 0x88000000;
    }
}
