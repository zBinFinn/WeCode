package org.zbinfinn.wecode.features;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.functioncategories.FunctionCategoryManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FunctionCategoryCommand extends Feature implements ClientCommandRegistrationCallback {

    @Override
    public void activate() {

    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("fun").executes(this::run)
        );
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        FunctionCategoryManager.cache();

        return 0;
    }
}
