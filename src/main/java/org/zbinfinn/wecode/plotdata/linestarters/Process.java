package org.zbinfinn.wecode.plotdata.linestarters;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.DFColors;
import org.zbinfinn.wecode.plotdata.LineStarter;

public class Process extends LineStarter {
    public Process(String name) {
        super(name);
    }

    @Override
    public Text getPrefix() {
        return Text.literal("[P]").withColor(DFColors.LIME.color);
    }

    @Override
    public String getType() {
        return "process";
    }
}
