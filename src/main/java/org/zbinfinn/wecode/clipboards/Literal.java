package org.zbinfinn.wecode.clipboards;

import net.minecraft.text.Text;

public class Literal implements Value {
    private String value;

    public Literal(String value) {
        this.value = value;
    }

    public String data() {
        return value;
    }

    @Override
    public Text render() {
        return Text.literal(value).withColor(0x55FFFF);
    }

    @Override
    public String value() {
        return value;
    }
}
