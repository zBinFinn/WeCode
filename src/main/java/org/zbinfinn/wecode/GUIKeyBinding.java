package org.zbinfinn.wecode;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.zbinfinn.wecode.mixin.MKeyBindingAccessor;

public class GUIKeyBinding extends KeyBinding {
    private final boolean enabledInChat;

    public GUIKeyBinding(String translationKey, InputUtil.Type type, int code, String category, boolean enabledInChat) {
        super(translationKey, type, code, category);
        this.enabledInChat = enabledInChat;
    }

    public GUIKeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        this(translationKey, type, code, category, false);
    }

    @Override
    public boolean isPressed() {
        if (WeCode.MC.currentScreen instanceof ChatScreen && !enabledInChat) {
            return false;
        }

        return (InputUtil.isKeyPressed(
                WeCode.MC.getWindow().getHandle(),
                ((MKeyBindingAccessor) this).getBoundKey().getCode()));
    }

}
