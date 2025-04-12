package org.zbinfinn.wecode.template_editor.refactor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.templates.CodeBlocks;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.templates.codeblock.EntityEvent;
import dev.dfonline.flint.templates.codeblock.Function;
import dev.dfonline.flint.templates.codeblock.PlayerEvent;
import dev.dfonline.flint.templates.codeblock.Process;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.linestarters.Event;
import org.zbinfinn.wecode.template_editor.TemplateEditor;
import org.zbinfinn.wecode.template_editor.token.Token;
import org.zbinfinn.wecode.template_editor.token.TokenType;

import java.util.List;

public abstract class TedUtil {
    public static LineStarter getLineStarterFromTemplate(Template template) {
        CodeBlocks codeBlocks = template.getBlocks();
        LineStarter lineStarter = null;
        if (codeBlocks.getBlocks().getFirst() instanceof Function fun) {
            lineStarter = new org.zbinfinn.wecode.plotdata.linestarters.Function(fun.getFunctionName());
        } else if (codeBlocks.getBlocks().getFirst() instanceof Process proc) {
            lineStarter = new org.zbinfinn.wecode.plotdata.linestarters.Process(proc.getProcessName());
        } else if (codeBlocks.getBlocks().getFirst() instanceof PlayerEvent playerEvent) {
            lineStarter = new Event(playerEvent.getAction());
        } else if (codeBlocks.getBlocks().getFirst() instanceof EntityEvent entityEvent) {
            lineStarter = new Event(entityEvent.getAction());
        }
        return lineStarter;
    }

    public record CursorTokenPosition(int tokenIndex, int indexInTokenText) {}
    public static CursorTokenPosition getTokenIndexFromCursor(List<Token> tokens, int cursor) {
        int currentPos = 0;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int tokenStart = currentPos;
            int tokenEnd = tokenStart + token.text.length();

            if (cursor >= tokenStart && cursor <= tokenEnd) {
                int charInTokenIndex = cursor - tokenStart;
                return new CursorTokenPosition(i, charInTokenIndex);
            }

            currentPos = tokenEnd;
        }

        return new CursorTokenPosition(0, 0);
    }

    public record Pos(int x, int y) {}
    public static Pos getCursorPositionOnScreen(String string, int cursor) {
        TextRenderer tr = WeCode.MC.textRenderer;
        int x = TedConstants.Dimensions.editorX();
        int y = TedConstants.Dimensions.editorY();

        for (int i = 0; i < cursor; i++) {
            char c = string.charAt(i);
            if (c == '\n') {
                x = TedConstants.Dimensions.editorX();
                y += WeCode.MC.textRenderer.fontHeight;
                continue;
            }
            x += WeCode.MC.textRenderer.getWidth(String.valueOf(c));
        }

        return new Pos(x + 4, y + Flint.getClient().textRenderer.fontHeight/2);
    }
}
