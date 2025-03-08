package org.zbinfinn.wecode.util;

public class NumberUtil {
    public static double lerp(double value, double goal, double step) {
        if (value > goal) {
            value -= step;
            if (value < goal) {
                value = goal;
            }
        }
        if (value < goal) {
            value += step;
            if (value > goal) {
                value = goal;
            }
        }

        return value;
    }

    public static double hotLerp(double from, double to, double percentage) {
        double lerped = percentage < 0.5 ? 4 * Math.pow(percentage, 3) : (percentage - 1) * (2 * percentage - 2) * (2 * percentage - 2) + 1;
        return from + (to - from) * lerped;
    }
}
