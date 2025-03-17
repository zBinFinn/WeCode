package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public class NotificationTestCommand extends CommandFeature {
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
            case "mod_error" -> NotificationHelper.NotificationType.MOD_ERROR;
            case "mod_success" -> NotificationHelper.NotificationType.MOD_SUCCESS;
            case "mod_neutral" -> NotificationHelper.NotificationType.MOD_NORMAL;
            default -> NotificationHelper.NotificationType.NEUTRAL;
        };

        NotificationHelper.sendNotification(text, type, durationSeconds);
        return 1;
    }
}
