package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.ClickEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayerJoinCommand extends CommandFeature {
    private boolean locating = false;
    private long initialTime = 0;
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("pjoin").then(argument("player", EntityArgumentType.player()).executes(this::playerJoin))
        );
    }

    private int playerJoin(CommandContext<FabricClientCommandSource> context) {
        if (locating) {
            NotificationHelper.sendFailNotification("You already ran /pjoin", 3);
            locating = false;
            return 0;
        }

        String playerName = context.getInput().split(" ")[1];

        if (playerName.equals(WeCode.MC.player.getName().getString())) {
            NotificationHelper.sendFailNotification("You can't /pjoin yourself", 3);
            return 0;
        }

        initialTime = System.currentTimeMillis();
        locating = true;
        CommandSender.queue("locate " + playerName);
        return 0;
    }

    @Override
    public void handlePacket(Packet<?> packetU, CallbackInfo ci) {
        if (!(packetU instanceof GameMessageS2CPacket packet)) {
            return;
        }
        if (!locating) {
            return;
        }

        if (System.currentTimeMillis() - initialTime > 1000) {
            NotificationHelper.sendFailNotification("/pjoin request timed out", 3);
            locating = false;
            return;
        }

        String message = packet.content().getString();
        String[] split = message.split("\n");
        if (split.length == 4) {
            String line2 = split[1];
            if (line2.matches("[a-zA-Z0-9_\\-]+ is currently at spawn")) {
                NotificationHelper.sendFailNotification("That player isnt on any game", 3);
            }
            return;
        }

        if (split.length != 8) {
            return;
        }

        if (!split[1].matches("[a-zA-Z0-9_\\-]+ is currently playing on:")) {
            return;
        }
        if (!split[3].startsWith("→ ") || !split[4].startsWith("→ ") || !split[5].startsWith("→ ") || !split[6].startsWith("→ ")) {
            return;
        }
        ClickEvent event = packet.content().getStyle().getClickEvent();
        if (event.getAction() != (ClickEvent.Action.RUN_COMMAND)) {
            return;
        }
        CommandSender.queue(packet.content().getStyle().getClickEvent().getValue().substring(1));
        locating = false;
        ci.cancel();
    }
}
