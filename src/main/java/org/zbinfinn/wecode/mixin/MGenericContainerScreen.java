package org.zbinfinn.wecode.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.features.Features;
import org.zbinfinn.wecode.features.ParamDisplay;

@Mixin(GenericContainerScreen.class)
public class MGenericContainerScreen {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ((ParamDisplay) (Features.getFeature(ParamDisplay.class))).onChestRender(context);
    }
}
