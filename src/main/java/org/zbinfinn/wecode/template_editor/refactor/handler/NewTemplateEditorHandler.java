package org.zbinfinn.wecode.template_editor.refactor.handler;

import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.network.packet.Packet;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.template_editor.refactor.Parser;
import org.zbinfinn.wecode.template_editor.refactor.Tokenizer;
import org.zbinfinn.wecode.template_editor.refactor.gui.NewTemplateEditor;
import org.zbinfinn.wecode.template_editor.refactor.gui.NewTemplateScreen;

public class NewTemplateEditorHandler implements PacketListeningFeature, TickedFeature {
    private NewTemplateScreen screen = null;
    private final TemplateGrabberHandler grabberHandler = new TemplateGrabberHandler();
    private final TemplatePlacerHandler placerHandler = new TemplatePlacerHandler();

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

    public void placeTemplate(NewTemplateEditor editor) {
        try {
            Template template = new Parser(new Tokenizer(editor.getText()).tokenize(true)).parse();
            LineStarter lineStarter = editor.getLineStarter();
            placeTemplate(template, lineStarter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void placeTemplate(Template template, LineStarter lineStarter) {
        placerHandler.place(template, lineStarter);
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        grabberHandler.packet(packet);
        placerHandler.packet(packet);

        return EventResult.PASS;
    }

    @Override
    public void tick() {
        grabberHandler.tick();
        placerHandler.tick();
    }
}
