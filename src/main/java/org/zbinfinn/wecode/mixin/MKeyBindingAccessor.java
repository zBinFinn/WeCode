package org.zbinfinn.wecode.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface MKeyBindingAccessor {
    @Accessor("boundKey")
    InputUtil.Key getBoundKey();
}
