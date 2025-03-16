package org.zbinfinn.wecode.features.commands.expressioncommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.List;
import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ExpressionCommand extends Feature implements ClientCommandRegistrationCallback {
    @Override
    public void activate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("exp").then(
                        argument("expression", StringArgumentType.greedyString())
                                .executes(this::run)
                )
        );
    }

    private Optional<String> parse(String expression) {
        Optional<List<Token>> tokens = Tokenizer.tokenize(expression);
        if (tokens.isEmpty()) {
            NotificationHelper.sendFailNotification("Failed to tokenize expression", 3);
            return Optional.empty();
        }
        for (Token token : tokens.get()) {
            MessageHelper.debug("Token: " + token);
        }

        return Optional.empty();
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        String expression = StringArgumentType.getString(context, "expression");
        MessageHelper.debug("Expression: " + expression);
        Optional<String> parsed = parse(expression);
        if (parsed.isEmpty()) {
            return 1;
        }
        MessageHelper.debug("Parsed: " + parsed);

        return 0;
    }
}
