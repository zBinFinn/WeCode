package org.zbinfinn.wecode.features;

import dev.dfonline.flint.data.DFItem;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;

import java.util.Set;

public class DisplayLoreWhenEditingFeature implements RenderedFeature, TickedFeature {
    @Override
    public boolean alwaysOn() {
        return true;
    }

    private static final Set<String> COMMANDS_WHEN_TO_SHOW = Set.of(
        "i", "item", "lore",
        "addlore", "relore",
        "ila", "ils"
    );

    private ItemStack item;
    private int yOffset = 0;
    private boolean shouldDisplay = false;


    @Override
    public void render(DrawContext draw, RenderTickCounter renderTickCounter) {
        if (!shouldDisplay) {
            return;
        }
        if (item == null || item.isEmpty()) {
            return;
        }
        WeCode.drawCustomTooltip(draw, item, WeCode.MC.getWindow().getScaledWidth(), WeCode.MC.getWindow().getScaledHeight() - yOffset);
    }

    @Override
    public void tick() {
        if (WeCode.MC.player == null) {
            return;
        }
        gatherItem();
        shouldDisplay = checkShouldRender();
    }

    private void gatherItem() {
        item = WeCode.MC.player.getMainHandStack().copy();
        DFItem dfItem = new DFItem(item);
        yOffset = 17;
        for (Text line : dfItem.getLore()) {
            yOffset += WeCode.MC.textRenderer.fontHeight + 1;
        }
    }

    private boolean checkShouldRender() {
        if (!(WeCode.MC.currentScreen instanceof ChatScreen chatScreen)) {
            return false;
        }

        String text = chatScreen.chatField.getText();
        for (String command : COMMANDS_WHEN_TO_SHOW) {
            if (text.startsWith("/" + command + " ")) {
                return true;
            }
        }
        return false;
    }
}
