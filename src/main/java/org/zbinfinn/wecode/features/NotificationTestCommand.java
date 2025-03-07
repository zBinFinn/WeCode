package org.zbinfinn.wecode.features;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public class NotificationTestCommand extends Feature implements ClientCommandRegistrationCallback{
    @Override
    public void activate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                ClientCommandManager.literal("notification").then(
                        ClientCommandManager.argument("type", StringArgumentType.string()).then(
                                ClientCommandManager.argument("duration", DoubleArgumentType.doubleArg()).then(
                                        ClientCommandManager.argument("text", StringArgumentType.greedyString()).executes(this::run)
                                )
                        )
                )
        );
    }

    private int run(CommandContext<FabricClientCommandSource> commandContext) {
        String typeStr = StringArgumentType.getString(commandContext, "type");
        String text = StringArgumentType.getString(commandContext, "text");
        double durationSeconds = DoubleArgumentType.getDouble(commandContext, "duration");

        NotificationHelper.NotificationType type = switch (typeStr.toLowerCase()) {
            case "error" -> NotificationHelper.NotificationType.ERROR;
            case "success" -> NotificationHelper.NotificationType.SUCCESS;
            default -> NotificationHelper.NotificationType.NEUTRAL;
        };

        NotificationHelper.sendNotification(text, type, durationSeconds);
        return 1;
    }
}
