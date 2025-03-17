package org.zbinfinn.wecode.clipboards;

import net.minecraft.text.Text;

public class Color implements Value {
    private String color;

    public Color(String color) {
        this.color = color;
    }

    public String data() {
        return color;
    }

    @Override
    public Text render() {
        return Text.literal(color).styled((style -> style.withColor(Integer.parseInt(color.substring(1), 16))));
    }

    @Override
    public String value() {
        return "<" + color + ">";
    }
}
