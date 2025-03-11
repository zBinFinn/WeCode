package org.zbinfinn.wecode.plotdata.linestarters;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.DFColors;
import org.zbinfinn.wecode.plotdata.LineStarter;

public class Function extends LineStarter {
    public Function(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return "function";
    }

    @Override
    public Text getPrefix() {
        return Text.literal("[F]").withColor(DFColors.SKY.color);
    }
}
