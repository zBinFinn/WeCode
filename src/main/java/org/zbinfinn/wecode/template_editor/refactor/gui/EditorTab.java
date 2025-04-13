package org.zbinfinn.wecode.template_editor.refactor.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.ClickableWidget;
import org.zbinfinn.wecode.util.StringUtil;

public class EditorTab extends ClickableWidget {
    private final NewTemplateEditor editor;
    private final TextRenderer tr;
    public EditorTab(Positioning pos, NewTemplateEditor editor, ClickAction action) {
        super(pos, action);
        this.editor = editor;
        this.tr = WeCode.MC.textRenderer;
    }

    public NewTemplateEditor getEditor() {
        return editor;
    }

    @Override
    public void render(DrawContext draw, float delta) {
        draw.fill(pos().getX(), pos().getY(), pos().getRightX(), pos().getBottomY(), getColor());
        draw.drawText(tr, StringUtil.trimToLength(editor.getName(), pos().getWidth(), ".."),
                      pos().getX(), pos().getY(), 0xFFFFFF, true);
    }

    private int getColor() {
        if (editor.isSelected()) {
            return 0xAA888888;
        }
        if (isHovered()) {
            return 0xAA444444;
        }
        return 0xAA000000;
    }
}
