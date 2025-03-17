package org.zbinfinn.wecode.features;

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.playerstate.ModeState;
import org.zbinfinn.wecode.playerstate.SpawnState;

public class AutoChatFeature extends Feature {
    boolean expectingChatChange = false;
    long timeout;

    @Override
    public void changeState(ModeState oldState, ModeState newState) {
        if (newState instanceof SpawnState) {
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
    public void receiveChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!expectingChatChange) {
            return;
        }

        String message = packet.content().getString();
        switch (message) {
            case "» Chat is now set to Global. You will now see messages from players on your node. Use /chat to change it again.":
            case "» Chat is now set to Local. You will only see messages from players on your plot. Use /chat to change it again.":
            case "» Chat is now set to None. Public chat will be blocked. Use /chat to change it again.":
            case "» Chat is now set to Do Not Disturb. Public chat and messages will be blocked. Use /chat to change it again.":
                expectingChatChange = false;
                ci.cancel();
                break;
            default:
                break;
        }
    }
}
