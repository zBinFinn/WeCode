package org.zbinfinn.wecode.template_editor;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.ScreenHandler;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TemplateEditorCommands implements CommandFeature {
    @Override
    public String commandName() {
        return "teditor";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder.then(
            literal("open")
                .executes(context -> {
                    TemplateEditorHandler.open();
                    return 0;
                })
        ).then(
            literal("template")
                .executes(context -> {
                    TemplateEditorHandler.addTemplateItem(Flint.getUser().getPlayer().getMainHandStack());
                    return 0;
                })
        ).then(
            literal("resetDANGEROUS")
                .executes(context -> {
                    TemplateEditorHandler.reset();
                    return 0;
                })
        );
    }
}