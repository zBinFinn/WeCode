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

}
