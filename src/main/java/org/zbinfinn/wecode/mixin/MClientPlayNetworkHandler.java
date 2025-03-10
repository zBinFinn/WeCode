package org.zbinfinn.wecode.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.features.Features;

@Mixin(ClientPlayNetworkHandler.class)
public class MClientPlayNetworkHandler {
    @Unique
    private boolean sending = false;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (sending) {
            return;
        }
        ci.cancel();
        message = Features.handleChatMessage(message);

        ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) (Object) this;
        sending = true;
        handler.sendChatMessage(message);
        sending = false;
    }
}
