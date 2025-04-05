package org.zbinfinn.wecode.features.functionsearch;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.hypercube.Mode;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.zbinfinn.wecode.GUIKeyBinding;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.PlotDataManager;

public class FunctionSearch implements TickedFeature {
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

    public FunctionSearch() {
        KeyBindingHelper.registerKeyBinding(keyBinding);
        KeyBindingHelper.registerKeyBinding(keyBindingCTRL);
    }

    @Override
    public void tick() {
        if (!(Flint.getUser().getMode() == Mode.DEV)) {
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
