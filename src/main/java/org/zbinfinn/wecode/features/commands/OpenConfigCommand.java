package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.features.Feature;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OpenConfigCommand extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("wecode").executes(commandContext -> {
                    ScreenHandler.scheduleOpenScreen(Config.getConfig().getLibConfig().generateScreen(null));
                    return 0;
                })


        );

    }
}
