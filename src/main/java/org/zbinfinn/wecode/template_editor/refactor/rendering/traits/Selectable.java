package org.zbinfinn.wecode.template_editor.refactor.rendering.traits;

public interface Selectable extends GUIElement {
    default void onSelect() {};
    default void onDeselect() {};
}
