package org.zbinfinn.wecode.features.keybinds;

import dev.dfonline.flint.feature.trait.RenderedFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.feature.trait.TooltipRenderFeature;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;
import org.zbinfinn.wecode.GUIKeyBinding;
import org.zbinfinn.wecode.WeCode;

import java.util.List;

public class PinItemKeybindFeature implements TickedFeature, TooltipRenderFeature, RenderedFeature {
    private final GUIKeyBinding pinKeyBind = new GUIKeyBinding(
            "key.wecode.pinitem",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_RIGHT_ALT,
            "key.wecode.category"
    );

    private ItemStack pinnedItem;

    public PinItemKeybindFeature() {
        KeyBindingHelper.registerKeyBinding(pinKeyBind);
    }

    @Override
    public void tick() {
        if (!pinKeyBind.isPressed()) {
            return;
        }
        pinnedItem = null;
    }

    @Override
    public void tooltipRender(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {
        if (WeCode.isDrawingCustomTooltip()) {
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
    public void render(DrawContext draw, RenderTickCounter renderTickCounter) {
        if (pinnedItem == null) {
            return;
        }
        Matrix3x2fStack stack = draw.getMatrices();

        stack.pushMatrix();
        draw.state.goUpLayer();

        WeCode.drawingCustomTooltip = true;
        draw.drawItemTooltip(WeCode.MC.textRenderer, pinnedItem, draw.getScaledWindowWidth(), 20);
        WeCode.drawingCustomTooltip = false;

        draw.state.goDownLayer();
        stack.popMatrix();
    }
}
