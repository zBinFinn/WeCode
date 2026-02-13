package org.zbinfinn.wecode.features;

import dev.dfonline.flint.feature.trait.TickedFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.util.ItemUtil;

public class ParamDisplay implements TickedFeature {
    private ItemStack refBook = null;
    private @Nullable String itemInstance = null;

    private static final String REF_BOOK_NAME = "◆ Reference Book ◆";

    @Override
    public void tick() {
        if (WeCode.MC.player == null) {
            return;
        }
        for (ItemStack item : WeCode.MC.player.getInventory().getMainStacks()) {
            if (item.getItem().equals(Items.AIR)) {
                continue;
            }
            if (item.getName().getString().equals(REF_BOOK_NAME)) {
                if (itemInstance == null || !itemInstance.equals(ItemUtil.getItemTags(item).getString("hypercube:item_instance").orElse(""))) {
                    itemInstance = ItemUtil.getItemTags(item).getString("hypercube:item_instance").orElse(null);
                }
            }

            if (ItemUtil.getItemTags(item).getString("hypercube:item_instance").orElse("").equals(itemInstance)) {
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

        context.state.goUpLayer();

        WeCode.drawingCustomTooltip = true;
        context.drawItemTooltip(WeCode.MC.textRenderer, refBook, WeCode.MC.getWindow().getScaledWidth(), 20);
        WeCode.drawingCustomTooltip = false;
    }

    @Override
    public boolean isEnabled() {
        return Config.getConfig().ParameterDisplay;
    }
}
