package org.zbinfinn.wecode.features.debug;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.Color;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.playerstate.*;

import java.util.ArrayList;
import java.util.Collection;

import static net.minecraft.text.Text.literal;

public class StateDisplay extends DebugFeature {
    private int y = 5;
    @Override
    public void hudRender(DrawContext draw, RenderTickCounter tickCounter) {
        ArrayList<Text> generalText = renderGeneralState(WeCode.generalState);
        ArrayList<Text> stateText = new ArrayList<>();

        if (WeCode.modeState instanceof SpawnState spawnState) {
            stateText = renderSpawnState(spawnState);
        }

        if (WeCode.modeState instanceof DevState devState) {
            stateText = renderDevState(devState);
        }

        if (WeCode.modeState instanceof BuildState buildState) {
            stateText = renderBuildState(buildState);
        }

        if (WeCode.modeState instanceof PlayState playState) {
            stateText = renderPlayState(playState);
        }

        y = 5;
        render(generalText, draw);
        render(stateText, draw);
    }

    private ArrayList<Text> renderPlayState(PlayState state) {
        ArrayList<Text> texts = new ArrayList<>();

        texts.add(literal("Play State: ").withColor(Color.PURPLE.color));

        return texts;
    }

    private ArrayList<Text> renderBuildState(BuildState state) {
        ArrayList<Text> texts = new ArrayList<>();

        texts.add(literal("Build State: ").withColor(Color.PURPLE.color));

        return texts;
    }

    private ArrayList<Text> renderDevState(DevState state) {
        ArrayList<Text> texts = new ArrayList<>();

        texts.add(literal("Dev State: ").withColor(Color.PURPLE.color));

        return texts;
    }

    private void render(ArrayList<Text> texts, DrawContext draw) {
        for (int i = 0; i < texts.size(); i++) {
            Text text = texts.get(i);
            if (i == 0) {
                draw.drawTextWithShadow(WeCode.MC.textRenderer, text, 5, y, 0xFF_FFFFFF);
                y += 2;
            } else {
                //draw.drawTextWithShadow(WeCode.MC.textRenderer, Text.literal(i + "").withColor(Color.LIGHT_PURPLE.color), 5, y, 0xFF_FFFFFF);
                draw.drawTextWithShadow(WeCode.MC.textRenderer, text, 5 + 5, y, 0xFF_FFFFFF);
            }
            y += WeCode.MC.textRenderer.fontHeight + 2;
        }
    }

    private ArrayList<Text> renderGeneralState(State state) {
        ArrayList<Text> texts = new ArrayList<>();
        texts.add(literal("General State:").withColor(Color.PURPLE.color));
        texts.add(literal("Node = ").withColor(Color.PURPLE.color)
                .append(literal(state.getNode().displayName).withColor(Color.LIGHT_PURPLE.color)));
        texts.add(literal(""));

        return texts;
    }

    private ArrayList<Text> renderSpawnState(SpawnState state) {
        ArrayList<Text> texts = new ArrayList<>();
        texts.add(literal("Spawn State:").withColor(Color.PURPLE.color));

        return texts;
    }
}
