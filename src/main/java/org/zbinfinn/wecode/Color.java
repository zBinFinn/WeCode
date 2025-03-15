package org.zbinfinn.wecode;

public enum Color {
    GREEN(0x88FF88),
    RED(0xFF8888),
    BLUE(0x8888FF),
    PURPLE(0xFF88FF),
    LIGHT_PURPLE(0xFFAAFF),
    YELLOW(0xFFCC88);

    public final int color;

    Color(int color) {
        this.color = color;
    }
}