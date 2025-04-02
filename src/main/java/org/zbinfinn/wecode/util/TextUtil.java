package org.zbinfinn.wecode.util;

import net.kyori.adventure.platform.modcommon.impl.NonWrappingComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class TextUtil {
    private static final Map<String, String> smallcapsMap = new HashMap<>();

    public static Text minimessage(String miniMsg) {
        return NonWrappingComponentSerializer.INSTANCE.serialize(MiniMessage.miniMessage().deserialize(miniMsg));
    }

    public static void init() {
        initSmallCapsMap();
    }

    public static String smallcaps(String input) {
        String[] split = input.split("");
        StringBuilder out = new StringBuilder();
        for (String s : split) {
            out.append(smallcapsMap.getOrDefault(s.toLowerCase(), s));
        }
        return out.toString();
    }

    private static void initSmallCapsMap() {
        String[] base = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] mini = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ".split("");

        for (int i = 0; i < base.length; i++) {
            smallcapsMap.put(base[i], mini[i]);
        }
    }
}
