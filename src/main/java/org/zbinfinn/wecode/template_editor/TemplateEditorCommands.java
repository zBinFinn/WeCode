package org.zbinfinn.wecode.template_editor;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.template_editor.refactor.NewTemplateScreen;

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
                    WeCode.TEMPLATE_EDITOR_HANDLER.open();
                    return 0;
                })
        ).then(
            literal("template")
                .executes(context -> {
                    //WeCode.TEMPLATE_EDITOR_HANDLER.addTemplateItem(Flint.getUser().getPlayer().getMainHandStack());
                    return 0;
                })
        ).then(
            literal("resetDANGEROUS")
                .executes(context -> {
                    WeCode.TEMPLATE_EDITOR_HANDLER.reset();
                    return 0;
                })
        ).then(
            literal("menutest")
                .executes(context -> {
                    ScreenHandler.scheduleOpenScreen(new NewTemplateScreen());
                    return 0;
                })
        );
    }
}