package org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success;

import net.minecraft.text.Text;
import org.zbinfinn.wecode.features.chatmessagenotifs.Matcher;
import org.zbinfinn.wecode.helpers.MessageHelper;

import java.util.List;

public class JoinPlotMatcher extends Matcher {
    @Override
    public boolean matches(String message) {
        return message.startsWith("Joined game: ");
    }

    @Override
    public Text modify(Text text, String message) {
        List<Text> siblings = text.getSiblings();

        Text plotName = siblings.get(2);
        Text ownerName = siblings.get(4);

        return Text.literal("Joined: ").append(plotName).append(Text.literal(" by ")).append(ownerName);
    }

    @Override
    public double getDuration() {
        return 8;
    }
}
