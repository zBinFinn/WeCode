package org.zbinfinn.wecode.features.functionsearch;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;

public class FunctionSearchBox extends EditBoxWidget {
    public FunctionSearchBox(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.literal("Type here..."), Text.empty());
    }
}
