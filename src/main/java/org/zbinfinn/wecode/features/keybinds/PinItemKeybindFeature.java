package org.zbinfinn.wecode.features.keybinds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.GUIKeyBinding;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;

import java.util.List;

public class PinItemKeybindFeature extends Feature {
    private final GUIKeyBinding pinKeyBind = new GUIKeyBinding(
            "key.wecode.pinitem",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_RIGHT_ALT,
            "key.wecode.category"
    );
    /*private final GUIKeyBinding unpinKeyBind = new GUIKeyBinding(
            "key.wecode.unpinitem",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_O,
            "key.wecode.category"
    );*/

    private ItemStack pinnedItem;

    @Override
    public void activate() {
        KeyBindingHelper.registerKeyBinding(pinKeyBind);
        //KeyBindingHelper.registerKeyBinding(unpinKeyBind);
    }

    @Override
    public void tick() {
        if (!pinKeyBind.isPressed()) {
            return;
        }
        pinnedItem = null;
    }

    @Override
    public void tooltip(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list, boolean isCustom) {
        if (isCustom) {
            return;
        }
        if (!pinKeyBind.isPressed()) {
            return;
        }
        if (pinnedItem != null && pinnedItem.equals(item)) {
            return;
        }

        pinnedItem = item.copy();
    }

    @Override
    public void hudRender(DrawContext draw, RenderTickCounter tickCounter) {
        if (pinnedItem == null) {
            return;
        }
        MatrixStack stack = draw.getMatrices();

        stack.push();
        stack.translate(0, 0, 5000);
        WeCode.isDrawingCustomTooltip = true;
        draw.drawItemTooltip(WeCode.MC.textRenderer, pinnedItem, draw.getScaledWindowWidth(), 20);
        WeCode.isDrawingCustomTooltip = false;
        stack.pop();
    }
}
