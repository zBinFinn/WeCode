package org.zbinfinn.wecode.features;

import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.UserMessageListeningFeature;
import dev.dfonline.flint.util.result.EventResult;
import dev.dfonline.flint.util.result.ReplacementEventResult;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.zbinfinn.wecode.PacketSender;
import org.zbinfinn.wecode.clipboards.ClipBoards;

public class ColorSpaceApplicator implements PacketListeningFeature, UserMessageListeningFeature {
    private boolean currentlySending = false;
    @Override
    public EventResult onSendPacket(Packet<?> packet) {
        if (currentlySending) {
            return EventResult.PASS;
        }
        if (packet instanceof CommandExecutionC2SPacket commandExecutionC2SPacket) {
            return handleCommand(commandExecutionC2SPacket);
        }

        return EventResult.PASS;
    }

    private EventResult handleCommand(CommandExecutionC2SPacket commandExecutionC2SPacket) {
        String content = commandExecutionC2SPacket.command();
        content = ClipBoards.replaceAll(content);
        currentlySending = true;
        PacketSender.sendPacket(new CommandExecutionC2SPacket(content));
        currentlySending = false;
        return EventResult.CANCEL;
    }

    @Override
    public ReplacementEventResult<String> sendMessage(String message) {
        return ReplacementEventResult.replace(ClipBoards.replaceAll(message));
    }
}
