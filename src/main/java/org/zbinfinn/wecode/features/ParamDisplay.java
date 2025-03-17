package org.zbinfinn.wecode.features;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.util.ItemUtil;

public class ParamDisplay extends Feature {
    private ItemStack refBook = null;
    private String itemInstance = null;

    private static final String REF_BOOK_NAME = "◆ Reference Book ◆";

    @Override
    public void tick() {
        for (ItemStack item : WeCode.MC.player.getInventory().main) {
            if (item.getItem().equals(Items.AIR)) {
                continue;
            }
            if (item.getName().getString().equals(REF_BOOK_NAME)) {
                if (itemInstance == null || !itemInstance.equals(ItemUtil.getItemTags(item).getString("hypercube:item_instance"))) {
                    itemInstance = ItemUtil.getItemTags(item).getString("hypercube:item_instance");
                }
            }

            if (ItemUtil.getItemTags(item).getString("hypercube:item_instance").equals(itemInstance)) {
                if (item.getName().getString().equals(REF_BOOK_NAME)) {
                    continue;
                }
                refBook = item.copy();
                return;
            }
        }
        refBook = null;
    }

    public void onChestRender(DrawContext context) {
        if (refBook == null) {
            return;
        }
        if (WeCode.MC.currentScreen == null) {
            return;
        }
        if (!(WeCode.MC.currentScreen instanceof GenericContainerScreen)) {
            return;
        }

        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(0, 0, 50);

        WeCode.isDrawingCustomTooltip = true;
        context.drawItemTooltip(WeCode.MC.textRenderer, refBook, WeCode.MC.getWindow().getScaledWidth(), 20);
        WeCode.isDrawingCustomTooltip = false;

        stack.pop();
    }

    @Override
    public boolean isEnabled() {
        return Config.getConfig().ParameterDisplay;
    }
}
