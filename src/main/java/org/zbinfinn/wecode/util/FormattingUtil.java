package org.zbinfinn.wecode.util;

import java.text.DecimalFormat;

public class FormattingUtil {
    public static String numberAsString(double number, int digitsAfterZero) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < digitsAfterZero; i++) {
            pattern.append("0");
        }

        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
        return decimalFormat.format(number);
    }
}
