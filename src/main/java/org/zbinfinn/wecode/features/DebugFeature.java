package org.zbinfinn.wecode.features;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.helpers.MessageHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DebugFeature extends Feature implements ClientCommandRegistrationCallback {
    private boolean sentPackets = false;
    private boolean receivedPackets = false;

    @Override
    public void activate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("debug")
                        .then(literal("sentpackets").executes(this::toggleSentPackets))
                        .then(literal("receivedpackets").executes(this::toggleReceivedPackets))
        );
    }

    @Override
    public void handlePacket(Packet<?> packet, CallbackInfo ci) {
        if (!receivedPackets) {
            return;
        }
        MessageHelper.debug("Packet received: " + packet.getClass().getName());
    }

    @Override
    public void sentPacket(Packet<?> packet, CallbackInfo ci) {
        if (!sentPackets) {
            return;
        }
        if (packet instanceof ClientTickEndC2SPacket) {
            return;
        }
        if (packet instanceof RequestCommandCompletionsC2SPacket realPacket) {
            MessageHelper.debug("Packet received: " + packet.getClass().getName());
            MessageHelper.debug("Partial Command: " + realPacket.getPartialCommand());
            return;
        }

        MessageHelper.debug("Packet sent: " + packet.getClass().getName());
    }

    private int toggleReceivedPackets(CommandContext<FabricClientCommandSource> context) {
        receivedPackets = !receivedPackets;
        return 0;
    }

    private int toggleSentPackets(CommandContext<FabricClientCommandSource> context) {
        sentPackets = !sentPackets;
        return 0;
    }


}
