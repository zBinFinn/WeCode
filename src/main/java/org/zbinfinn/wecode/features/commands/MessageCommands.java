package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.Regexes;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MessageCommands extends CommandFeature {
    private final Pattern MSG_RECEIVED_REGEX = Pattern.compile(
            "\\[(" + Regexes.PLAYER_NAME + ") → You] .+"
    );
    private final Pattern MSG_SENT_REGEX = Pattern.compile(
            "\\[You → (" + Regexes.PLAYER_NAME + ")] .+"
    );

    private String lastReceivedPlayer;
    private String lastSentPlayer;

    @Override
    public void receiveChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        Text content = packet.content();

        Matcher receivedMatcher = MSG_RECEIVED_REGEX.matcher(content.getString());
        if (receivedMatcher.find()) {
            lastReceivedPlayer = receivedMatcher.group(1);
        }

        Matcher sentMatcher = MSG_SENT_REGEX.matcher(content.getString());
        if (sentMatcher.find()) {
            lastSentPlayer = sentMatcher.group(1);
        }
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("r")
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(this::respond)
        ));
        commandDispatcher.register(
                literal("l")
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(this::last)
        ));
    }

    private int last(CommandContext<FabricClientCommandSource> context) {
        if (lastSentPlayer == null) {
            NotificationHelper.sendFailNotification("You haven't messaged a player yet", 3);
            return 1;
        }

        String message = StringArgumentType.getString(context, "message");
        CommandSender.queue("msg " + lastSentPlayer + " " + message);

        return 0;
    }

    private int respond(CommandContext<FabricClientCommandSource> context) {
        if (lastReceivedPlayer == null) {
            NotificationHelper.sendFailNotification("You haven't been messaged by a player yet", 3);
            return 1;
        }

        String message = StringArgumentType.getString(context, "message");
        CommandSender.queue("msg " + lastReceivedPlayer + " " + message);
        return 0;
    }
}
