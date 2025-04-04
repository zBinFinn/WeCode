package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.template.CodeBuilder;
import dev.dfonline.flint.template.Template;
import dev.dfonline.flint.template.block.CodeBlock;
import dev.dfonline.flint.template.block.impl.PlayerAction;
import dev.dfonline.flint.util.message.impl.prefix.ErrorMessage;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.item.ItemStack;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;

import java.util.ArrayList;
import java.util.Random;

public class TemplateEditorHandler {
    public static TemplateEditorScreen screen = new TemplateEditorScreen();

    public static void open() {
        screen.init(WeCode.MC, WeCode.MC.getWindow().getScaledWidth(), WeCode.MC.getWindow().getScaledHeight());
        ScreenHandler.scheduleOpenScreen(screen);
    }

    public static void addTemplateItem(ItemStack mainHandStack) {
        try {
            Template template = new Template("Test " + new Random().nextInt(5, 99), "Author");
            ArrayList<CodeBlock> blocks = CodeBuilder
                .create()
                .add(new PlayerAction("SendMessage"))
                .build();
            screen.addTemplate(template);
        } catch (Exception err) {
            Flint.getUser().sendMessage(
                new ErrorMessage("Something went wrong while adding a template item :(")
            );
            err.printStackTrace();
        }
    }

    public static void reset() {
        screen = new TemplateEditorScreen();
    }
}
