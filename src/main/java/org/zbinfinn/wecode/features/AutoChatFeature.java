package org.zbinfinn.wecode.features;

import dev.dfonline.flint.feature.trait.ChatListeningFeature;
import dev.dfonline.flint.feature.trait.ModeSwitchListeningFeature;
import dev.dfonline.flint.hypercube.Mode;
import dev.dfonline.flint.util.result.ReplacementEventResult;
import net.kyori.adventure.text.Component;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.playerstate.SpawnState;

public class AutoChatFeature implements ModeSwitchListeningFeature, ChatListeningFeature {
    boolean expectingChatChange = false;
    long timeout;

    @Override
    public void onSwitchMode(Mode oldMode, Mode newMode) {
        if (newMode == Mode.SPAWN) {
            return;
        }
        CommandSender.queue("c " + Config.getConfig().PreferredChatMode.identifier);
        expectingChatChange = true;
        timeout = System.currentTimeMillis() + 2000;
    }

    @Override
    public boolean isEnabled() {
        return Config.getConfig().AutoChatMode;
    }

    @Override
    public ReplacementEventResult<Text> onChatMessage(Text text, boolean b) {
        if (!expectingChatChange) {
            return ReplacementEventResult.pass();
        }

        String message = text.getString();
        switch (message) {
            case "» Chat is now set to Global. You will now see messages from players on your node. Use /chat to change it again.":
            case "» Chat is now set to Local. You will only see messages from players on your plot. Use /chat to change it again.":
            case "» Chat is now set to None. Public chat will be blocked. Use /chat to change it again.":
            case "» Chat is now set to Do Not Disturb. Public chat and messages will be blocked. Use /chat to change it again.":
                expectingChatChange = false;
                return ReplacementEventResult.cancel();
        }

        return ReplacementEventResult.pass();
    }
}
