package org.zbinfinn.wecode.template_editor.refactor.rendering.traits;

import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;

public interface GUIElement {
    Positioning getPositioning();
    default Positioning pos() {
        return getPositioning();
    }
}
