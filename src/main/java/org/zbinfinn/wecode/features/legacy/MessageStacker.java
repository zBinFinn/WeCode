package org.zbinfinn.wecode.features.legacy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;

public class MessageStacker extends Feature {
    private int counter = 1;
    @Override
    public void handlePacket(Packet<?> packetO, CallbackInfo ci) {
        if (true) return;
        if (!(packetO instanceof GameMessageS2CPacket packet)) {
            return;
        }

        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();

        if (chatHud.messages.isEmpty()) {
            return;
        }

        ChatHudLine latestMessage = WeCode.MC.inGameHud.getChatHud().messages.getFirst();
        Text latestContent = latestMessage.content();

        StringBuilder content = new StringBuilder(latestMessage.content().getString());
        int index = content.lastIndexOf(" (" + counter + ")");
        if (index != -1) {
            content.replace(index, index + (" (" + counter + ")").length(), "");
        }

        String actualContent = content.toString();

        if (!actualContent.equals(packet.content().getString())) {
            counter = 1;
            return;
        }


        ci.cancel();

        counter++;
        chatHud.messages.removeFirst();
        chatHud.addMessage(packet.content().copy().append(Text.literal(" (" + counter + ")").withColor(0x888888)));

        chatHud.refresh();
    }

    @Override
    public boolean isEnabled() {
        // Legacy
        return false;
    }
}
