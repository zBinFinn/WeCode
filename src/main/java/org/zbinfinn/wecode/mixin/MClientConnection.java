package org.zbinfinn.wecode.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.features.Features;
import org.zbinfinn.wecode.functioncategories.FunctionCategoryManager;

@Mixin(ClientConnection.class)
public class MClientConnection {
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void weCode$handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        Features.handlePacket(packet, ci);
        FunctionCategoryManager.receivePacket(packet);
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void weCode$sendPacket(Packet<?> packet, CallbackInfo ci) {
        Features.sentPacket(packet, ci);
    }
}
