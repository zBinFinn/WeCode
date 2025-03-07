package org.zbinfinn.wecode.features;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;

public class FlightSpeedKeybindFeature extends Feature {
    private final int NORMAL_SPEED = 100;
    private final int FAST_SPEED = 300;
    private final KeyBinding keyBinding = new KeyBinding(
            "key.wecode.flightspeed",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_R,
            "key.wecode.category"
    );

    @Override
    public void activate() {
        KeyBindingHelper.registerKeyBinding(keyBinding);
    }

    @Override
    public void tick() {
        if (!keyBinding.wasPressed()) {
            return;
        }

        if (WeCode.MC.player.getAbilities().getFlySpeed() >= flySpeedFromPercentage(FAST_SPEED)) {
            CommandSender.queue("fs " + NORMAL_SPEED);
        } else {
            CommandSender.queue("fs " + FAST_SPEED);
        }

    }

    private int flySpeedToPercentage(float speed) {
        return (int) (speed * 2000);
    }
    private float flySpeedFromPercentage(int percentage) {
        return (float) percentage / 2000;
    }
}
