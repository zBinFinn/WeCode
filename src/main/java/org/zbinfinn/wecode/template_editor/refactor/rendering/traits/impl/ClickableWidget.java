package org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl;

import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;

public abstract class ClickableWidget extends Widget {
    public interface ClickAction {
        void onClick(ClickableWidget widget, int button);
    }

    private ClickAction action;
    protected ClickableWidget(Positioning pos, ClickAction action) {
        super(pos);
        this.action = action;
    }

    @Override
    public void click(int button, double mouseX, double mouseY) {
        action.onClick(this, button);
    }
}
