package org.zbinfinn.wecode.features;

import dev.dfonline.flint.feature.trait.TickedFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix3x2fStack;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.util.ItemUtil;

import java.util.Optional;

public class ParamDisplay implements TickedFeature {
    private ItemStack refBook = null;
    private String itemInstance = null;

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

            var tags = ItemUtil.getItemTags(item);
            if (tags == null) {
                continue;
            }

            if (item.getName().getString().equals(REF_BOOK_NAME)) {
                Optional<String> itemInstanceOpt = tags.getString("hypercube:item_instance");
                itemInstanceOpt.ifPresent(s -> itemInstance = s);
            }

            Optional<String> itemInstanceOpt = tags.getString("hypercube:item_instance");
            if (itemInstanceOpt.isPresent() && itemInstanceOpt.get().equals(itemInstance)) {
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

        context.state.goDownLayer();
    }

    @Override
    public boolean isEnabled() {
        return Config.getConfig().ParameterDisplay;
    }
}
