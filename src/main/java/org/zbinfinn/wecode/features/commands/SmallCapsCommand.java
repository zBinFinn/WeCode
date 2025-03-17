package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.util.TextUtil;

public class SmallCapsCommand extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                ClientCommandManager.literal("smallcaps").then(ClientCommandManager.argument("text", StringArgumentType.greedyString()).executes((commandContext -> {
                            String text = commandContext.getArgument("text", String.class);
                            CommandSender.queue("txt " + TextUtil.smallcaps(text) + " 1");
                            return 1;
                        }))
                ));
    }
}
