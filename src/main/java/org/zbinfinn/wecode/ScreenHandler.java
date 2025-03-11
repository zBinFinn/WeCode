package org.zbinfinn.wecode;

import net.minecraft.client.gui.screen.Screen;
import org.zbinfinn.wecode.helpers.MessageHelper;

public class ScreenHandler {
    private static Screen screen;
    public static void scheduleOpenScreen(Screen screen) {
        ScreenHandler.screen = screen;
    }

    public static void tick() {
        if (ScreenHandler.screen != null) {
            WeCode.MC.setScreen(ScreenHandler.screen);
            ScreenHandler.screen = null;
        }
    }
}
