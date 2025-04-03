package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.template_editor.token.Token;

// Testing Discord Webhook A

public class TemplateEditorScreen extends Screen {
    private final int SCREEN_WIDTH = Flint.getClient().getWindow().getScaledWidth();
    private final int SCREEN_HEIGHT = Flint.getClient().getWindow().getScaledHeight();

    private final int TEMPLATE_EDITOR_PADDING_DOWN = 25;
    private final int TEMPLATE_EDITOR_X = 10;
    private final int TEMPLATE_EDITOR_Y = 10;
    private final int TEMPLATE_EDITOR_WIDTH = SCREEN_WIDTH - TEMPLATE_EDITOR_X * 2;
    private final int TEMPLATE_EDITOR_HEIGHT = SCREEN_HEIGHT - TEMPLATE_EDITOR_Y * 2 - TEMPLATE_EDITOR_PADDING_DOWN;

    private final int SAVE_BUTTON_HEIGHT = 20;
    private final int SAVE_BUTTON_WIDTH = 60;
    private final int SAVE_BUTTON_X = TEMPLATE_EDITOR_X + TEMPLATE_EDITOR_WIDTH + 2 - SAVE_BUTTON_WIDTH;
    private final int SAVE_BUTTON_Y = TEMPLATE_EDITOR_Y + TEMPLATE_EDITOR_HEIGHT + 5; //+ SAVE_BUTTON_HEIGHT;


    private final TemplateEditor templateEditor = new TemplateEditor(TEMPLATE_EDITOR_X, TEMPLATE_EDITOR_Y,
                                                                     TEMPLATE_EDITOR_WIDTH, TEMPLATE_EDITOR_HEIGHT);

    private final ButtonWidget saveButton =
        new ButtonWidget.Builder(Text.literal("SAVE"), (this::saveButton))
            .dimensions(SAVE_BUTTON_X, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT)
            .tooltip(Tooltip.of(Text.literal("Save Template")))
            .build();

    private void saveButton(ButtonWidget buttonWidget) {
        // TODO:
        Tokenizer tokenizer = new Tokenizer(templateEditor.getText());
        var tokens = tokenizer.tokenize(false);
        for (Token token : tokens) {
            System.out.println(token.debugString());
        }
    }


    public TemplateEditorScreen() {
        super(Text.literal("Template Editor"));

        saveButton.setNavigationOrder(1);
        addDrawableChild(saveButton);
        addDrawableChild(templateEditor);
        setFocused(templateEditor);
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        /*if (keyCode == GLFW.GLFW_KEY_TAB) {
            if (templateEditor.isFocused()) {
                setFocused(saveButton);
            } else if (saveButton.isFocused()) {
                setFocused(templateEditor);
            }
            return true;
        }*/
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
