package org.zbinfinn.wecode.features;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OpenConfigCommand extends Feature implements ClientCommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("wecode").executes(commandContext -> {
                    NotificationHelper.sendFailNotification("This command is currently work in progress", 3);
                    ScreenHandler.setScreenToConfig();
                    return 0;
                })
        );
    }

    @Override
    public void activate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }
}
