package org.zbinfinn.wecode.plotdata.linestarters;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.DFColors;
import org.zbinfinn.wecode.plotdata.LineStarter;

public class Event extends LineStarter {
    public Event(String name) {
        super(name);
    }

    @Override
    public Text getPrefix() {
        if (name.contains("Player") || name.equals("Purchase Product") || name.equals("Resource Pack Decline") || name.equals("Resource Pack Load") || name.equals("View VIP Perks")) {
            return Text.literal("[E]").withColor(DFColors.AQUA_LIGHT.color);
        } else {
            return Text.literal("[E]").withColor(DFColors.YELLOW_LIGHT.color);
        }
    }

    @Override
    public String getType() {
        return "event";
    }
}
