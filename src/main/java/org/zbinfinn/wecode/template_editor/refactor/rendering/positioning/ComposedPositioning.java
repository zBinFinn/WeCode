package org.zbinfinn.wecode.template_editor.refactor.rendering.positioning;

import org.zbinfinn.wecode.WeCode;

public record
ComposedPositioning(Value x, Value y, Value width, Value height) implements Positioning {

    public interface Value {
        int calculate(int total);
    }

    public record PercentageValue(double percentage) implements Value {
        @Override
        public int calculate(int total) {
            return (int) (total * percentage);
        }
    }
    public record StaticValue(int value) implements Value {
        @Override
        public int calculate(int total) {
            return value;
        }
    }

    @Override
    public int getX() {
        return x.calculate(WeCode.MC.getWindow().getScaledWidth());
    }

    @Override
    public int getY() {
        return y.calculate(WeCode.MC.getWindow().getScaledHeight());
    }

    @Override
    public int getWidth() {
        return width.calculate(WeCode.MC.getWindow().getScaledWidth());
    }

    @Override
    public int getHeight() {
        return height.calculate(WeCode.MC.getWindow().getScaledHeight());
    }

    @Override
    public double getXPercent() {
        return (double) WeCode.MC.getWindow().getScaledWidth() / getWidth();
    }

    @Override
    public double getYPercent() {
        return (double) WeCode.MC.getWindow().getScaledHeight() / getHeight();
    }
}
