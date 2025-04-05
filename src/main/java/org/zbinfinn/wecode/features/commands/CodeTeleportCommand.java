package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.CommandSender;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class CodeTeleportCommand implements CommandFeature {
    @Override
    public String commandName() {
        return "dtp";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder.then(argument("args", StringArgumentType.greedyString()).executes(this::run));
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        String args = StringArgumentType.getString(context, "args");

        CommandSender.queue("dev");
        CommandSender.queueDelay("ctp " + args, 100);
        return 0;
    }
}
