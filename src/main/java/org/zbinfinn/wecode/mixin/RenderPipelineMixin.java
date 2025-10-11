package org.zbinfinn.wecode.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.zbinfinn.wecode.helpers.RenderHelper;

import java.util.Optional;

@Mixin(RenderPipeline.class)
public class RenderPipelineMixin {

    @WrapMethod(method = "getBlendFunction", remap = false)
    private Optional<BlendFunction> modifyBlendFunction(Operation<Optional<BlendFunction>> original) {
        if (RenderHelper.blendFunction != null) return Optional.of(RenderHelper.blendFunction);
        return original.call();
    }

}
