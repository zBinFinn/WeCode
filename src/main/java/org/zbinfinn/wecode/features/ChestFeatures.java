package org.zbinfinn.wecode.features;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

import static org.zbinfinn.wecode.features.Features.chestFeatures;
import static org.zbinfinn.wecode.features.Features.features;

public class ChestFeatures {
    public static void onScreenOpen(HandledScreen<?> screen) {
        features().forEach(Feature::closeChest);
        features().forEach(feat -> feat.openChest(screen));
    }

    public static void onScreenClosed() {
        features().forEach(Feature::closeChest);
    }

    public static void onRender(DrawContext context, int mouseX, int mouseY, int x, int y, float delta) {
        chestFeatures().forEach(feat -> feat.render(context, mouseX, mouseY, x, y, delta));
    }

    public static void onDrawSlot(DrawContext context, Slot slot) {
        chestFeatures().forEach(feat -> feat.drawSlot(context, slot));
    }

    public static ItemStack onGetHoverStack(Slot instance) {
        return chestFeatures().map(feat -> feat.getHoverStack(instance)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return chestFeatures().anyMatch(feature -> feature.mouseClicked(mouseX, mouseY, button));
    }

    public static boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return chestFeatures().anyMatch(feature -> feature.keyPressed(keyCode, scanCode, modifiers));
    }

    public static boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return chestFeatures().anyMatch(feature -> feature.keyReleased(keyCode, scanCode, modifiers));
    }

    public static boolean onCharTyped(char chr, int modifiers) {
        return chestFeatures().anyMatch(feature -> feature.charTyped(chr, modifiers));
    }

    public static void onClickSlot(Slot slot, int button, SlotActionType actionType, int syncId, int revision) {
        chestFeatures().forEach(feature -> feature.clickSlot(slot, button, actionType, syncId, revision));
    }

    public static boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return chestFeatures().anyMatch(feature -> feature.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
    }

}
