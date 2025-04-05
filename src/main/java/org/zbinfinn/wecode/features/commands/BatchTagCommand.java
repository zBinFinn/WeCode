package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class BatchTagCommand implements CommandFeature {
    @Override
    public String commandName() {
        return "batchtag";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> literalArgumentBuilder, CommandRegistryAccess commandRegistryAccess) {
        return literalArgumentBuilder
                .then(
                        argument("args", StringArgumentType.greedyString()).executes(
                                this::run
                        ));
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        String args = StringArgumentType.getString(context, "args");
        String[] argsArray = args.split(" ");

        if (argsArray.length % 2 != 0) {
            NotificationHelper.sendNotificationWithSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED, 0.5f, 1f, Text.literal("Expected an even number of arguments"), NotificationHelper.NotificationType.MOD_ERROR, 3);
            return 1;
        }

        if (WeCode.MC.player.getMainHandStack().isEmpty()) {
            NotificationHelper.sendNotificationWithSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED, 0.5f, 1f, Text.literal("You are not holding an item"), NotificationHelper.NotificationType.MOD_ERROR, 3);
            return 2;
        }

        for (int i = 0; i < argsArray.length; i += 2) {
            String key = argsArray[i];
            String value = argsArray[i + 1];
            CommandSender.queue("i tag set " + key + " " + value);
        }

        return 0;
    }
}
