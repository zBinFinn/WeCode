package org.zbinfinn.wecode.mixin;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.WeCode;

@Mixin(ToastManager.class)
public class MToastManager {

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void add(Toast toast, CallbackInfo ci) {
        if (toast.getType() == SystemToast.Type.UNSECURE_SERVER_WARNING) ci.cancel();

        WeCode.MC.getNetworkHandler().sendChatMessage("Hi");
    }
}
