package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.Regexes;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class ColorCommand extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("color").then(
                        argument("color", StringArgumentType.greedyString()).executes(
                                this::run
                        )
                )
        );
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        String color = StringArgumentType.getString(context, "color");
        if (!color.matches(Regexes.HEX_COLOR)) {
            NotificationHelper.sendFailNotification("That is NOT a hex color, expected format: #XXXXXX", 3);
            return 1;
        }

        if (WeCode.MC.player.getMainHandStack().getItem() == Items.WHITE_DYE) {
            CommandSender.queue("par color " + color);
            return 0;
        }

        CommandSender.queue("i color hex " + color);


        return 0;
    }
}
