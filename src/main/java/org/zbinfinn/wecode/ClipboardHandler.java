package org.zbinfinn.wecode;

public class ClipboardHandler {

    public static String getClipboard() {
        return WeCode.MC.keyboard.getClipboard();
    }

    public static void setClipboard(String content) {
        WeCode.MC.keyboard.setClipboard(content);
    }
}
