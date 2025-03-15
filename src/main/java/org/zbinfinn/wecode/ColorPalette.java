package org.zbinfinn.wecode;

import net.minecraft.text.Text;

public class ColorPalette {

    public static Text withColor(String text, Color color) {
        return Text.literal(text).withColor(color.color);
    }

    public static void init() {

    }
}
