package org.zbinfinn.wecode.helpers;

import net.minecraft.text.Text;

public class DebugHelper {
    public static void printSiblings(Text text) {
        int i = 0;
        MessageHelper.debug(Text.literal("Siblings of: ").append(text));
        for (Text sibling : text.getSiblings()) {
            MessageHelper.debug(Text.literal("Sibling " + i + " : ").append(sibling));
            i++;
        }
    }
}
