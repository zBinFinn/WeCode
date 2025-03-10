package org.zbinfinn.wecode.functioncategories;

import net.minecraft.network.packet.Packet;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.MessageHelper;

public class FunctionCategoryManager {
    public static boolean caching = false;

    public static void cache() {
        caching = true;
        CommandSender.run("ctp");
        while (true) {
            if (WeCode.MC.player)
        }
    }

    public static void receivePacket(Packet<?> packet) {
        MessageHelper.debug(packet.getClass().getSimpleName());
    }

}
