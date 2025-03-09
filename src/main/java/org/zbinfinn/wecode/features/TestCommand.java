package org.zbinfinn.wecode.features;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.util.TemplateUtil;

public class TestCommand extends Feature implements ClientCommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                ClientCommandManager.literal("test").executes(commandContext -> {
                    if (TemplateUtil.fromItem(WeCode.MC.player.getMainHandStack()).getCodeBlocks().isEmpty()) {
                        return 0;
                    }

                    MessageHelper.debug(TemplateUtil.fromItem(WeCode.MC.player.getMainHandStack()).getCodeBlocks().getFirst().getBlock());

                    return 0;
                })
        );
    }

    @Override
    public void activate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }
}
