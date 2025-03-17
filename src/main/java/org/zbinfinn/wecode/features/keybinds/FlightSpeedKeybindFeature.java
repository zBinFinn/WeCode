package org.zbinfinn.wecode.features.keybinds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;

public class FlightSpeedKeybindFeature extends Feature {
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

        if (WeCode.MC.player.getAbilities().getFlySpeed() >= flySpeedFromPercentage(Config.getConfig().FastFlightSpeed)) {
            CommandSender.queue("fs " + Config.getConfig().NormalFlightSpeed);
        } else {
            CommandSender.queue("fs " + Config.getConfig().FastFlightSpeed);
        }

    }

    private int flySpeedToPercentage(float speed) {
        return (int) (speed * 2000);
    }
    private float flySpeedFromPercentage(int percentage) {
        return (float) percentage / 2000;
    }
}
