package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.trait.ChatListeningFeature;
import dev.dfonline.flint.feature.trait.CommandFeature;
import dev.dfonline.flint.util.result.ReplacementEventResult;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class PlayerJoinCommand implements CommandFeature, ChatListeningFeature {
    private boolean locating = false;
    private long initialTime = 0;

    @Override
    public String commandName() {
        return "pjoin";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder.then(argument("player", EntityArgumentType.player()).executes(this::playerJoin));
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
    public ReplacementEventResult<Text> onChatMessage(Text text, boolean b) {
        if (!locating) {
            return ReplacementEventResult.pass();
        }

        if (System.currentTimeMillis() - initialTime > 1000) {
            NotificationHelper.sendFailNotification("/pjoin request timed out", 3);
            locating = false;
            return ReplacementEventResult.pass();
        }

        String message = text.getString();
        String[] split = message.split("\n");
        if (split.length == 4) {
            String line2 = split[1];
            if (line2.matches("[a-zA-Z0-9_\\-]+ is currently at spawn")) {
                NotificationHelper.sendFailNotification("That player isnt on any game", 3);
            }
            return ReplacementEventResult.pass();
        }

        if (split.length != 8) {
            return ReplacementEventResult.pass();
        }

        if (!split[1].matches("[a-zA-Z0-9_\\-]+ is currently playing on:")) {
            return ReplacementEventResult.pass();
        }
        if (!split[3].startsWith("→ ") || !split[4].startsWith("→ ") || !split[5].startsWith("→ ") || !split[6].startsWith("→ ")) {
            return ReplacementEventResult.pass();
        }
        ClickEvent event = text.getStyle().getClickEvent();
        if (event.getAction() != (ClickEvent.Action.RUN_COMMAND)) {
            return ReplacementEventResult.pass();
        }
        CommandSender.queue(text.getStyle().getClickEvent().getValue().substring(1));
        locating = false;
        return ReplacementEventResult.cancel();
    }
}
