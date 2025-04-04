package org.zbinfinn.wecode.template_editor;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TemplateTabButton extends ButtonWidget {
    private final int id;
    public TemplateTabButton(int x, int y, int width, int height, Text message, PressAction onPress, int id) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
