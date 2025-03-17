package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.features.Feature;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CodeTeleportCommand extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("dtp").then(argument("args", StringArgumentType.greedyString()).executes(this::run)));
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        String args = StringArgumentType.getString(context, "args");

        CommandSender.queue("dev");
        CommandSender.queueDelay("ctp " + args, 100);
        return 0;
    }
}
