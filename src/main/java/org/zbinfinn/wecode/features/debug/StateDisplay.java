package org.zbinfinn.wecode.features.debug;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.User;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.Color;
import org.zbinfinn.wecode.WeCode;

import java.util.ArrayList;

import static net.minecraft.text.Text.literal;

public class StateDisplay implements DebugFeature, RenderedFeature {
    private int y;

    @Override
    public void render(DrawContext draw, RenderTickCounter renderTickCounter) {
        User user = Flint.getUser();
        ArrayList<Text> texts = new ArrayList<>();
        texts.add(literal("General State:").withColor(Color.PURPLE.color));
        texts.add(fancy("Node = ", (user.getNode() == null) ? "null" : user.getNode().getName()));
        texts.add(fancy("Plot = ", (user.getPlot() == null) ? "null" : (user.getPlot().getName().getString())));
        texts.add(fancy("Mode = ", (user.getMode() == null) ? "null" : (user.getMode().getName())));

        render(texts, draw);
    }

    private static Text fancy(String first, String second) {
        return literal(first).withColor(Color.PURPLE.color)
                .append(literal(second).withColor(Color.LIGHT_PURPLE.color));
    }

    private void render(ArrayList<Text> texts, DrawContext draw) {
        y = 5;
        for (int i = 0; i < texts.size(); i++) {
            Text text = texts.get(i);
            if (i == 0) {
                draw.drawTextWithShadow(WeCode.MC.textRenderer, text, 5, y, 0xFF_FFFFFF);
                y += 2;
            } else {
                draw.drawTextWithShadow(WeCode.MC.textRenderer, text, 5 + 5, y, 0xFF_FFFFFF);
            }
            y += WeCode.MC.textRenderer.fontHeight + 2;
        }
    }
}
