package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.NotificationHelper;

public class BatchTagCommand extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                ClientCommandManager.literal("batchtag").then(
                        ClientCommandManager.argument("args", StringArgumentType.greedyString()).executes(commandContext -> {
                            String args = StringArgumentType.getString(commandContext, "args");
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
                        })));
    }
}
