package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class NotificationTestCommand implements CommandFeature {

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

    @Override
    public String commandName() {
        return "";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> literalArgumentBuilder, CommandRegistryAccess commandRegistryAccess) {
        return literalArgumentBuilder.then(
                argument("type", StringArgumentType.string()).then(
                        argument("duration", DoubleArgumentType.doubleArg()).then(
                                argument("text", StringArgumentType.greedyString()).executes(this::run)
                        )
                )
        );
    }
}
