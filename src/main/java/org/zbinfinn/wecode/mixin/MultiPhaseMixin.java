package org.zbinfinn.wecode.mixin;

import net.minecraft.client.render.RenderLayer;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zbinfinn.wecode.helpers.RenderHelper;

@Mixin(RenderLayer.MultiPhase.class)
public class MultiPhaseMixin {

    @ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/DynamicUniforms;write(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;F)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"), index = 1)
    private Vector4fc modifyColorModulator(Vector4fc colorModulator) {
        if (RenderHelper.colorModulator != null) return RenderHelper.colorModulator;
        return colorModulator;
    }

}