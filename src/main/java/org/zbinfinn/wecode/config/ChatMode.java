package org.zbinfinn.wecode.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum ChatMode implements NameableEnum {
    GLOBAL("g"),
    LOCAL("l"),
    NONE("n"),
    DND("dnd");

    public final String identifier;
    ChatMode(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("wecode.config.autochat.preferredmode." + name().toLowerCase());
    }
}
