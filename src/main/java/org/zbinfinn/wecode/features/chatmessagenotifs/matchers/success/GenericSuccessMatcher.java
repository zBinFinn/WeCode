package org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

public class GenericSuccessMatcher extends Matcher {

    @Override
    public boolean matches(String message) {
        return true;
    }

    @Override
    public Text modify(Text text, String message) {
        return Text.literal(message);
    }
}
