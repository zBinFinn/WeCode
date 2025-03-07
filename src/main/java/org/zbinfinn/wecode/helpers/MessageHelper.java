package org.zbinfinn.wecode.helpers;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.zbinfinn.wecode.WeCode;

public class MessageHelper {
    public static void debug(Text message) {
        send(Text.literal("♮ ").styled(style -> style.withColor(0x888888)).append(message.copy().styled(style -> style.withColor(TextColor.fromRgb(0xffffff)))));
    }
    public static void debug(String message) {
        debug(Text.literal(message));
    }

    private static void send(Text message) {
        if (WeCode.MC.player == null) {
            return;
        }
        WeCode.MC.player.sendMessage(message, false);
    }
}
