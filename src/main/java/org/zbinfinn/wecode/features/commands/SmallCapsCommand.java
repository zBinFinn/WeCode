package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.util.TextUtil;

import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class SmallCapsCommand implements CommandFeature {
    @Override
    public String commandName() {
        return "smallcaps";
    }

    @Override
    public Set<String> aliases() {
        return Set.of(
                "sc"
        );
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder
                .then(argument("text", StringArgumentType.greedyString()).executes((commandContext -> {
                            String text = commandContext.getArgument("text", String.class);
                            CommandSender.queue("txt " + TextUtil.smallcaps(text) + " 1");
                            return 1;
                        }))
                );
    }
}
