package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.util.message.impl.prefix.ErrorMessage;
import net.minecraft.item.ItemStack;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;


public class TemplateEditorHandler {
    public static TemplateEditorScreen screen = new TemplateEditorScreen();

    public static void open() {
        screen.init(WeCode.MC, WeCode.MC.getWindow().getScaledWidth(), WeCode.MC.getWindow().getScaledHeight());
        ScreenHandler.scheduleOpenScreen(screen);
    }

    public static void addTemplateItem(ItemStack mainHandStack) {
        try {
            Template template = Template.fromItem(mainHandStack);
            //Template template = Template.fromJson("{\"name\":\"Exported\",\"author\":\"WeCode TEditor\",\"code\":\"H4sIAAAAAAAA/62QsQrCMBCGX0VudnDOKOimS92klJieNTRNanKCteTdvbQdROmgOOXuv8vHx/VwMk7VAcSxB12CGHtYTq+A1sgOfSEVaWc5nwoBGdpyhyHIClPsK4Ywg7CZaFwNSaLSnXiplCRTZGWDHB4w0CIjr20FMS4hGEcgVjHnZsZGn4tR6NVkz7jN9SZN+FHksdZ2q62dtfBS1Th81B4Vj8G1mK5BXZsA1vkGZqX/c0LlmvZTPaPO6HD5wlwZF/BdPY9PrNT83AoCAAA=\"}");
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
