package org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;

public class GenericButton extends ClickableWidget {
    Text text;

    public GenericButton(Positioning pos, ClickAction action, Text text) {
        super(pos, action);
        this.text = text;
    }

    @Override
    public void render(DrawContext draw, float delta) {
        draw.fill(
            pos().getX(), pos().getY(),
            pos().getRightX(), pos().getBottomY(),
            getColor()
        );
        draw.drawCenteredTextWithShadow(
            WeCode.MC.textRenderer,
            text,
            pos().getX() + pos().getWidth() / 2,
            pos().getY() + (pos().getHeight() - WeCode.MC.textRenderer.fontHeight + 1) / 2, 0xFFFFFF);
    }

    private int getColor() {
        return isHovered() ? 0xAA666666 : 0xAA000000;
    }
}
