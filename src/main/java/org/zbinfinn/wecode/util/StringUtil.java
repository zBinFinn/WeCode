package org.zbinfinn.wecode.util;

import org.zbinfinn.wecode.WeCode;

public abstract class StringUtil {
    public static String trimToLength(String string, int maxLength, String ending) {
        var tr = WeCode.MC.textRenderer;

        if (tr.getWidth(string) <= maxLength) {
            return string;
        }

        while (tr.getWidth(string) > (maxLength - tr.getWidth(ending))) {
            string = string.substring(0, string.length() - 1);
        }

        return string + ending;
    }
}
