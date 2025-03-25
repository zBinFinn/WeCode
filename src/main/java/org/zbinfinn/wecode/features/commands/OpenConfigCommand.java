package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.features.Feature;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OpenConfigCommand implements CommandFeature {
    @Override
    public String commandName() {
        return "wecode";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder.executes(context -> {
            ScreenHandler.scheduleOpenScreen(Config.getConfig().getLibConfig().generateScreen(null));
            return 0;
        });
    }
}
