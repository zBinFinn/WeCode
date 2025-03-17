package org.zbinfinn.wecode.features;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.PacketSender;
import org.zbinfinn.wecode.clipboards.ClipBoards;

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
        content = ClipBoards.replaceAll(content);
        ci.cancel();
        currentlySending = true;
        PacketSender.sendPacket(new CommandExecutionC2SPacket(content));
        currentlySending = false;
    }

    @Override
    public String handleChatMessage(String message) {
        message = ClipBoards.replaceAll(message);
        return message;
    }
}
