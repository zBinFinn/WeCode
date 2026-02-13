package org.zbinfinn.wecode.features.functionsearch;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;

public final class FunctionSearchBox {
    public static EditBoxWidget getFunctionSearchBox(
        TextRenderer textRenderer, int x, int y, int width, int height
    ) {
        return EditBoxWidget.builder()
            .x(x)
            .y(y)
            .placeholder(Text.literal("Type here..."))
            .build(textRenderer, width, height, Text.empty());
    }
}
