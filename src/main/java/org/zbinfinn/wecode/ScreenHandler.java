package org.zbinfinn.wecode;

import net.minecraft.client.gui.screen.Screen;
import org.zbinfinn.wecode.helpers.MessageHelper;

public class ScreenHandler {
    public static void setScreen(Screen screen) {
        WeCode.MC.executeSync(() -> {
            WeCode.MC.setScreen(screen);
        });
    }

    public static void setScreenToConfig() {
        WeCode.MC.executeSync(() -> {
           WeCode.MC.setScreen(Config.getConfig().getLibConfig().generateScreen(WeCode.MC.currentScreen));
        });
    }
}
