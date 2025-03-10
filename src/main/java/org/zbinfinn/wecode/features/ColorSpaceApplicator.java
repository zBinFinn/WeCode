package org.zbinfinn.wecode.features;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.PacketSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.colorspaces.ColorSpaces;
import org.zbinfinn.wecode.helpers.MessageHelper;

public class ColorSpaceApplicator extends Feature {
    private boolean currentlySending = false;
    @Override
    public void sentPacket(Packet<?> packet, CallbackInfo ci) {
        if (currentlySending) {
            return;

        }
        if (packet instanceof CommandExecutionC2SPacket commandExecutionC2SPacket) {
            handleCommand(commandExecutionC2SPacket, ci);
            return;
        }
    }

    private void handleCommand(CommandExecutionC2SPacket commandExecutionC2SPacket, CallbackInfo ci) {
        String content = commandExecutionC2SPacket.command();
        content = ColorSpaces.replaceAll(content);
        ci.cancel();
        currentlySending = true;
        PacketSender.sendPacket(new CommandExecutionC2SPacket(content));
        currentlySending = false;
    }

    @Override
    public String handleChatMessage(String message) {
        message = ColorSpaces.replaceAll(message);
        return message;
    }
}
