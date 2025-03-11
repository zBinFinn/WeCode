package org.zbinfinn.wecode;

public enum DFColors {
    AQUA_LIGHT(0xAAFFFF),
    AQUA_LIGHT_2(0x55FFFF),

    SKY(0x55AAFF),
    SKY_DARK(0x2A70D4),

    BLUE_DARK_2(0x0000AA),
    BLUE(0x5555FF),

    LIME(0xAAFF55),

    GREEN(0x55FF55),
    GREEN_DARK(0x2AD42A),

    YELLOW_LIGHT(0xFFFFAA),
    YELLOW_LIGHT_2(0xFFFFD4);

    public final int color;
    DFColors(int color) {
        this.color = color;
    }
}
