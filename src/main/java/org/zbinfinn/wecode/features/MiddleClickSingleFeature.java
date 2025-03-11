package org.zbinfinn.wecode.features;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.zbinfinn.wecode.Config;

public class MiddleClickSingleFeature extends Feature {

    @Override
    public boolean isEnabled() {
        return Config.getConfig().SingleMiddleClick;
    }

    @Override
    protected @Nullable ChestFeature makeChestFeature(HandledScreen<?> screen) {
        return new ChestFeature(screen) {
            @Override
            public boolean clickSlot(Slot slot, int button, SlotActionType actionType, int syncId) {
                if (actionType != SlotActionType.CLONE) {
                    return false;
                }

                return true;
            }
        };
    }
}
