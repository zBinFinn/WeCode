package org.zbinfinn.wecode.colorspaces;

import net.minecraft.text.Text;

public class Color {
    private String color;

    public Color(String color) {
        this.color = color;
    }

    public String toString() {
        return color;
    }

    public Text getColoredText() {
        return Text.literal(color).styled((style -> style.withColor(Integer.parseInt(color.substring(1), 16))));
    }
}
