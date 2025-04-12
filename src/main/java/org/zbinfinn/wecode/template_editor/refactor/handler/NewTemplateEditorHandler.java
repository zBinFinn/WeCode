package org.zbinfinn.wecode.template_editor.refactor.handler;

import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.network.packet.Packet;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.template_editor.refactor.gui.NewTemplateScreen;

public class NewTemplateEditorHandler implements PacketListeningFeature, TickedFeature {
    private NewTemplateScreen screen = null;
    private final TemplateGrabberHandler grabberHandler = new TemplateGrabberHandler();

    public NewTemplateEditorHandler() {}

    public void open() {
        if (screen == null) {
            screen = new NewTemplateScreen();
        }
        ScreenHandler.scheduleOpenScreen(screen);
    }

    public void reset() {
        screen = null;
    }

    public void addTemplateFromStarter(LineStarter lineStarter) {
        grabberHandler.grab(lineStarter, this::grabbedTemplate);
    }

    private void grabbedTemplate(Template template, LineStarter lineStarter) {
        if (template == null) {
            return;
        }
        screen.addTemplate(template, lineStarter);
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        grabberHandler.receivePacket(packet);

        return EventResult.PASS;
    }

    @Override
    public void tick() {
        grabberHandler.tick();
    }
}
