package org.zbinfinn.wecode.template_editor.refactor.rendering.positioning;

import org.zbinfinn.wecode.WeCode;

public record DynamicPositioning(double xPercent, double yPercent, double widthPercent, double heightPercent) implements Positioning {
    public int getX() {
        return round(xPercent * WeCode.MC.getWindow().getScaledWidth());
    }
    public int getY() {
        return round(yPercent * WeCode.MC.getWindow().getScaledHeight());
    }
    public int getWidth() {
        return round(widthPercent * WeCode.MC.getWindow().getScaledWidth());
    }
    public int getHeight() {
        return round(heightPercent * WeCode.MC.getWindow().getScaledHeight());
    }

    private int round(double value) {
        return (int) Math.round(value);
    }

    @Override
    public double getXPercent() {
        return xPercent;
    }

    @Override
    public double getYPercent() {
        return yPercent;
    }
}
