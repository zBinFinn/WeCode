package org.zbinfinn.wecode.template_editor.refactor.rendering.traits;

public interface Clickable extends GUIElement {
    /*
    Button:
    0 = LCLICK
    1 = RCLICK
    2 = MIDDLECLICK
    3+ = EXTRA BUTTONS
     */
    void click(int button, double mouseX, double mouseY);
}
