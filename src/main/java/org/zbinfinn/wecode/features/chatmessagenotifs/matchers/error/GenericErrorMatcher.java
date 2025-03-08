package org.zbinfinn.wecode.features.chatmessagenotifs.matchers.error;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;

public class GenericErrorMatcher extends Matcher {
    @Override
    public boolean matches(String message) {
        return true;
    }

    @Override
    public Text modify(Text text, String message) {
        return Text.literal(message);
    }
}
