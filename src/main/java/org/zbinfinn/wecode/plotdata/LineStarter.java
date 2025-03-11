package org.zbinfinn.wecode.plotdata;

import net.minecraft.text.Text;

public abstract class LineStarter {
    protected String name;
    public String getName() {
        return name;
    }
    public abstract Text getPrefix();

    public LineStarter(String name) {
        this.name = name;
    }

    public abstract String getType();
}
