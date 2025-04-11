package org.zbinfinn.wecode.template_editor.refactor.rendering.positioning;

public interface Positioning {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    double getXPercent();

    double getYPercent();

    default int getRightX() {
        return getX() + getWidth();
    };

    default int getBottomY() {
        return getY() + getHeight();
    };

    default boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() &&
            mouseX <= getRightX() &&
            mouseY >= getY() &&
            mouseY <= getBottomY();
    }
}
