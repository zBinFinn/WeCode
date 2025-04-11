package org.zbinfinn.wecode.template_editor.refactor.rendering.positioning;

import org.zbinfinn.wecode.WeCode;

public record FixedPositioning(int x, int y, int width, int height) implements Positioning {

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public double getXPercent() {
        return (double) WeCode.MC.getWindow().getScaledWidth() / width;
    }

    @Override
    public double getYPercent() {
        return (double) WeCode.MC.getWindow().getScaledHeight() / height;
    }
}
