package org.zbinfinn.wecode.template_editor.refactor.rendering.traits;

import net.minecraft.client.gui.DrawContext;

public interface Renderable extends GUIElement {
    void render(DrawContext draw, float delta);
}
