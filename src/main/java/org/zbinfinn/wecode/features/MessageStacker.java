package org.zbinfinn.wecode.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.Config;
import org.zbinfinn.wecode.DFColors;
import org.zbinfinn.wecode.WeCode;

public class MessageStacker extends Feature {
    private int counter = 1;
    @Override
    public void handlePacket(Packet<?> packetO, CallbackInfo ci) {
        if (!(packetO instanceof GameMessageS2CPacket packet)) {
            return;
        }

        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();

        if (chatHud.messages.isEmpty()) {
            return;
        }

        ChatHudLine latestMessage = WeCode.MC.inGameHud.getChatHud().messages.getFirst();
        Text latestContent = latestMessage.content();

        if (!latestContent.getString().startsWith(packet.content().getString())) {
            counter = 1;
            return;
        }

        ci.cancel();

        counter++;
        chatHud.messages.removeFirst();
        chatHud.refresh();
        chatHud.addMessage(packet.content().copy().append(Text.literal(" (" + counter + ")").withColor(0x888888)));
    }

    @Override
    public boolean isEnabled() {
        return Config.getConfig().MessageStacker;
    }
}
