package org.zbinfinn.wecode.helpers;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.zbinfinn.wecode.WeCode;

public abstract class MessageHelper {
    public static void debug(Text message) {
        send(Text.literal("♮ ").styled(style -> style.withColor(0x888888)).append(message.copy().styled(style -> style.withColor(TextColor.fromRgb(0xffffff)))));
    }
    public static void debug(String message) {
        debug(Text.literal(message));
    }

    public static void message(String message) {
        send(Text.literal(message));
    }

    public static void message(Text message) {
        send(message);
    }

    private static void send(Text message) {
        if (WeCode.MC.player == null) {
            return;
        }
        WeCode.MC.player.sendMessage(message, false);
    }

    public static void messageIndent(Text message, int indent) {
        message(Text.literal(" ".repeat(indent)).append(message));
    }

    public static void messageIndent(String message, int indent) {
        message(" ".repeat(indent) + message);
    }
}
