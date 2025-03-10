package org.zbinfinn.wecode;

import net.minecraft.text.Text;

public class ColorPalette {
    public enum Colors {
        GREEN(0x88FF88),
        RED(0xFF8888),
        BLUE(0x8888FF),
        PURPLE(0xFF88FF),
        LIGHT_PURPLE(0xFFAAFF),
        YELLOW(0xFFCC88);

        int color;
        Colors(int color) {
            this.color = color;
        }
    }

    public static Text withColor(String text, Colors color) {
        return Text.literal(text).withColor(color.color);
    }

    public static void init() {

    }
}
