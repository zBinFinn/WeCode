package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.plotdata.PlotDataManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CachePlotDataCommand extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("cache").executes(this::cache)
        );
    }

    private int cache(CommandContext<FabricClientCommandSource> context) {
        if (!WeCode.MC.player.isInCreativeMode()) {
            NotificationHelper.sendFailNotification("You can only do this in /dev mode", 3);
            return 1;
        }

        NotificationHelper.sendAppliedNotification("Starting Caching", 3);
        PlotDataManager.cacheLineStarters();
        return 0;
    }
}
