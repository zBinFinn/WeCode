package org.zbinfinn.wecode.features.functionsearch;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.zbinfinn.wecode.GUIKeyBinding;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.playerstate.DevState;
import org.zbinfinn.wecode.plotdata.PlotDataManager;

public class FunctionSearch extends Feature {
    FunctionSearchScreen screen;
    private final KeyBinding keyBinding = new KeyBinding(
            "key.wecode.functionsearch",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_H,
            "key.wecode.category"
    );
    private final GUIKeyBinding keyBindingCTRL = new GUIKeyBinding(
            "key.wecode.functionsearchctrl",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_F,
            "key.wecode.category"
    );

    @Override
    public void activate() {
        KeyBindingHelper.registerKeyBinding(keyBinding);
        KeyBindingHelper.registerKeyBinding(keyBindingCTRL);
    }

    @Override
    public void tick() {
        if (!(WeCode.modeState instanceof DevState)) {
            return;
        }
        if (!keyBinding.wasPressed()) {
            if (!(InputUtil.isKeyPressed(WeCode.MC.getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_CONTROL) && keyBindingCTRL.isPressed())) {
                return;
            }
        }
        if (WeCode.MC.currentScreen instanceof FunctionSearchScreen) {
            return;
        }
        PlotDataManager.cacheLineStarters();
        screen = new FunctionSearchScreen();
        ScreenHandler.scheduleOpenScreen(screen);
    }
}
