package org.zbinfinn.wecode.util;

public class LerpUtil {
    public static double easeOutExpo(double start, double end, double percentage) {
        return (percentage == 1 ? 1 : 1 - Math.pow(2, -10 * percentage)) * (end - start) + start;
    }

    public static double easeInOutBounce(double start, double end, double percentage) {
        percentage = percentage < 0.5
                ? (1 - easeOutBounce(0, 1, 1 - 2 * percentage)) / 2
                : (1 + easeOutBounce(0, 1, 2 * percentage - 1)) / 2;

        return start + (end - start) * percentage;
    }

    public static double easeOutBounce(double start, double end, double percentage) {
        double n1 = 7.5625;
        double d1 = 2.75;


        if (percentage < 1 / d1) {
            percentage = n1 * percentage * percentage;
        } else if (percentage < 2 / d1) {
            percentage = n1 * (percentage -= 1.5 / d1) * percentage + 0.75;
        } else if (percentage < 2.5 / d1) {
            percentage = n1 * (percentage -= 2.25 / d1) * percentage + 0.9375;
        } else {
            percentage = n1 * (percentage -= 2.625 / d1) * percentage + 0.984375;
        }

        return start + (end - start) * percentage;
    }

}
