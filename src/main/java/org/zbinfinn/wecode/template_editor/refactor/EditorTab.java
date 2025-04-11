package org.zbinfinn.wecode.template_editor.refactor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.ClickableWidget;

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
    public void render(DrawContext draw) {
        draw.fill(pos.getX(), pos.getY(), pos.getRightX(), pos.getBottomY(), getColor());
        draw.drawText(tr, Text.literal(editor.getName()), pos.getX(), pos.getY(), 0xFFFFFF, true);
    }

    private int getColor() {
        return editor.isSelected() ? 0xAA888888 : 0xAA000000;
    }
}
