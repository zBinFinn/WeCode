package org.zbinfinn.wecode.features.commands.expressioncommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.features.commands.expressioncommand.parser.ExpressionParser;
import org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer.ExpToken;
import org.zbinfinn.wecode.features.commands.expressioncommand.tokenizer.ExpTokenizer;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.List;
import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class ExpressionCommand implements CommandFeature {
    @Override
    public String commandName() {
        return "exp";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder
                .then(
                    argument("expression", StringArgumentType.greedyString())
                        .executes(this::run)
        );
    }

    private Optional<String> parse(String expression) {
        Optional<List<ExpToken>> tokens = ExpTokenizer.tokenize(expression);
        if (tokens.isEmpty()) {
            NotificationHelper.sendFailNotification("/exp: Failed to tokenize expression", 3);
            return Optional.empty();
        }

        ExpressionParser parser = new ExpressionParser(tokens.get());
        try {
            return Optional.of(parser.mathExpression());
        } catch (RuntimeException e) {
            NotificationHelper.sendFailNotification("/exp: " + e.getMessage(), 5);
        }

        return Optional.empty();
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        String expression = StringArgumentType.getString(context, "expression");
        Optional<String> parsed = parse(expression);
        if (parsed.isEmpty()) {
            return 1;
        }

        CommandSender.queue("num " + parsed.get());
        return 0;
    }
}
