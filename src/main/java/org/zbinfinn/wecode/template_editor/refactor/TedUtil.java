package org.zbinfinn.wecode.template_editor.refactor;

import dev.dfonline.flint.templates.CodeBlocks;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.templates.codeblock.EntityEvent;
import dev.dfonline.flint.templates.codeblock.Function;
import dev.dfonline.flint.templates.codeblock.PlayerEvent;
import dev.dfonline.flint.templates.codeblock.Process;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.linestarters.Event;

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
}
