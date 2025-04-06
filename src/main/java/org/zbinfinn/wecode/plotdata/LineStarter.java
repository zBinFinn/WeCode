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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LineStarter ls) {
            return name.equals(ls.getName()) && getType().equals(ls.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return "LineStarter [type= " + getType() + "name= " + getName() + "]";
    }
}
