package org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.KeyPressable;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.Selectable;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.Typable;

public abstract class EditTextWidget extends EditBox implements Typable, KeyPressable, Selectable {
    protected Positioning pos;
    private boolean selected = false;
    public EditTextWidget(TextRenderer textRenderer, int width, Positioning positioning) {
        super(textRenderer, width);
        this.pos = positioning;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void onSelect() {
        selected = true;
    }

    @Override
    public void onDeselect() {
        selected = false;
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        System.out.println("Char Typed: " + chr);
        setSelecting(Screen.hasShiftDown());
        replaceSelection(String.valueOf(chr));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return handleSpecialKey(keyCode);
    }

    @Override
    public Positioning getPositioning() {
        return pos;
    }
}
