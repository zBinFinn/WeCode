package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.impl.LocateFeature;
import dev.dfonline.flint.feature.trait.CommandFeature;
import dev.dfonline.flint.hypercube.Mode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class PlayerJoinCommand implements CommandFeature  {
    private boolean locating = false;

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

        locating = true;

        LocateFeature.requestLocate(playerName).thenAccept(locate -> {
            locating = false;

            if(locate == null) {
                NotificationHelper.sendFailNotification("Something went wrong", 3);
                return;
            }

            if(locate.mode() == Mode.SPAWN) {
                if(locate.node() != Flint.getUser().getNode())
                    CommandSender.queue("server " + locate.node().getId());
                
                NotificationHelper.sendFailNotification("That player is at spawn", 3);
                return;
            }

            CommandSender.queue("join  " + locate.plot().getId());
        });
        return 0;
    }
}
