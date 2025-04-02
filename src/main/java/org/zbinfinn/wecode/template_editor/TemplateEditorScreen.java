package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TemplateEditorScreen extends Screen {
    private final TemplateEditor templateEditor = new TemplateEditor(10, 10,
                                                                     Flint.getClient().getWindow().getScaledWidth() - 20,
                                                                     Flint.getClient().getWindow().getScaledHeight() - 20
    );

    public TemplateEditorScreen() {
        super(Text.literal("Template Editor"));
        addDrawable(templateEditor);
        setFocused(templateEditor);
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
    }
}
