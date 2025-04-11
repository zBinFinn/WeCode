package org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl;

import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.*;

public abstract class Widget implements Renderable, Clickable, Typable, Selectable, Hoverable {
    private boolean selected = false;
    public boolean isSelected() {
        return selected;
    }

    private boolean hovered = false;
    protected boolean isHovered() {
        return hovered;
    }

    protected final Positioning pos;
    protected Widget(Positioning pos) {
        this.pos = pos;
    }

    @Override
    public void click(int button, double mouseX, double mouseY) {

    }

    @Override
    public void charTyped(char chr, int modifiers) {

    }

    @Override
    public Positioning getPositioning() {
        return pos;
    }

    @Override
    public void onHover() {
        hovered = true;
    }

    @Override
    public void onUnhover() {
        hovered = false;
    }

    @Override
    public void onSelect() {
        selected = true;
    }

    @Override
    public void onDeselect() {
        selected = false;
    }
}
