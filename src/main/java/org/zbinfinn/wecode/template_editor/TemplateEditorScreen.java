package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TemplateEditorScreen extends Screen {
    private final int SCREEN_WIDTH = Flint.getClient().getWindow().getScaledWidth();
    private final int SCREEN_HEIGHT = Flint.getClient().getWindow().getScaledHeight();

    private final int TEMPLATE_EDITOR_PADDING_DOWN = 25;
    private final int TEMPLATE_EDITOR_X = 10;
    private final int TEMPLATE_EDITOR_Y = 10;
    private final int TEMPLATE_EDITOR_WIDTH = SCREEN_WIDTH - TEMPLATE_EDITOR_X * 2;
    private final int TEMPLATE_EDITOR_HEIGHT = SCREEN_HEIGHT - TEMPLATE_EDITOR_Y * 2 - TEMPLATE_EDITOR_PADDING_DOWN;

    private final int SAVE_BUTTON_HEIGHT = 15;
    private final int SAVE_BUTTON_WIDTH = 40;
    private final int SAVE_BUTTON_X = TEMPLATE_EDITOR_X + TEMPLATE_EDITOR_WIDTH + 2 - SAVE_BUTTON_WIDTH;
    private final int SAVE_BUTTON_Y = TEMPLATE_EDITOR_Y + TEMPLATE_EDITOR_HEIGHT + 5; //+ SAVE_BUTTON_HEIGHT;


    private final TemplateEditor templateEditor = new TemplateEditor(TEMPLATE_EDITOR_X, TEMPLATE_EDITOR_Y,
                                                                     TEMPLATE_EDITOR_WIDTH, TEMPLATE_EDITOR_HEIGHT);

    private final ButtonWidget saveButton =
        new ButtonWidget.Builder(Text.literal("SAVE"), this::buttonClick)
        .dimensions(SAVE_BUTTON_X, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT)
        .build();

    private void buttonClick(ButtonWidget buttonWidget) {
        System.out.println("Button Click");
    }

    public TemplateEditorScreen() {
        super(Text.literal("Template Editor"));
        addDrawable(templateEditor);
        addDrawable(saveButton);
        setFocused(templateEditor);
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
    }
}
