package org.zbinfinn.wecode.template_editor.refactor;

import net.minecraft.client.gui.DrawContext;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.ClickableWidget;
import org.zbinfinn.wecode.util.StringUtil;

public class TemplateLineStarterButton extends ClickableWidget {
    private final LineStarter lineStarter;
    private final String displayName;

    protected TemplateLineStarterButton(Positioning pos, ClickAction action, LineStarter lineStarter) {
        super(pos, action);
        this.lineStarter = lineStarter;
        displayName = StringUtil
            .trimToLength(lineStarter.getName(),
                          pos().getWidth(),
                          "..");
    }

    public LineStarter getLineStarter() {
        return lineStarter;
    }

    @Override
    public void render(DrawContext draw) {
        draw.fill(pos().getX(), pos().getY(), pos().getRightX(), pos().getBottomY(), isHovered() ? 0xAAFF8888 : 0xAA880000);
        draw.drawText(WeCode.MC.textRenderer,
                      displayName,
                      pos().getX(),
                      pos().getY() + (pos().getHeight() - WeCode.MC.textRenderer.fontHeight) / 2,
                      0xFFFFFF,
                      false
        );
    }
}
