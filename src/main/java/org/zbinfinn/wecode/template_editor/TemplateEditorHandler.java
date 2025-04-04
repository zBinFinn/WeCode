package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.template.ArgumentBuilder;
import dev.dfonline.flint.template.CodeBuilder;
import dev.dfonline.flint.template.Template;
import dev.dfonline.flint.template.block.CodeBlock;
import dev.dfonline.flint.template.block.impl.PlayerAction;
import dev.dfonline.flint.template.value.impl.NumberValue;
import dev.dfonline.flint.template.value.impl.StringValue;
import dev.dfonline.flint.template.value.impl.TextValue;
import dev.dfonline.flint.util.message.impl.prefix.ErrorMessage;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.client.font.TextRenderer;
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
                .add(new PlayerAction("SendMessage", ArgumentBuilder
                    .create()
                    .set(0, new StringValue("String Test"))
                    .set(1, new TextValue("<green>Text Test"))
                    .set(2, new NumberValue(5))
                    .set(3, new NumberValue(37.53))
                    .build()))
                .add(new PlayerAction("SendMessage", ArgumentBuilder
                    .create()
                    .set(2, new StringValue("String After 2 Empty Slots"))
                    .set(8, new StringValue("String After 5 MORE Empty Slots"))
                    .build()))
                .build();
            for (var block : blocks) {
                System.out.println("Block? " + block);
            }
            template.setBlocks(blocks);
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
