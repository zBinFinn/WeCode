package org.zbinfinn.wecode.template_editor.refactor;

import dev.dfonline.flint.templates.Template;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.PlotDataManager;
import org.zbinfinn.wecode.template_editor.TemplateParser;
import org.zbinfinn.wecode.template_editor.refactor.rendering.WecodeScreen;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.DynamicPositioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.ClickableWidget;

import java.util.ArrayList;
import java.util.List;

public class NewTemplateScreen extends WecodeScreen {
    private int currentEditorIndex = 0;
    private final List<NewTemplateEditor> editors = new ArrayList<>();
    private final List<EditorTab> tabs = new ArrayList<>();
    private final List<TemplateLineStarterButton> buttons = new ArrayList<>();

    private NewTemplateEditor currentEditor() {
        if (currentEditorIndex >= editors.size()) {
            return null;
        }
        return editors.get(currentEditorIndex);
    }

    private void addTemplateEditor(NewTemplateEditor editor) {
        editors.add(editor);
        setActiveEditor(editor);
        updateEditors();
    }

    private void updateEditors() {
        for (int i = 0; i < editors.size(); i++) {
            NewTemplateEditor editor = editors.get(i);
            removeElement(editor);
            if (i == currentEditorIndex) {
                addElement(editor);
            }
        }
        updateEditorTabs();
    }

    private void updateEditorTabs() {
        if (editors.isEmpty()) {
            return;
        }
        for (EditorTab button : tabs) {
            removeElement(button);
        }
        tabs.clear();
        double x = currentEditor().getPositioning().getXPercent();
        double y = currentEditor().getPositioning().getYPercent();
        for (NewTemplateEditor editor : editors) {
            EditorTab newTab = new EditorTab(new DynamicPositioning(x, y - TedConstants.EDITOR_TAB_HEIGHT, TedConstants.EDITOR_TAB_WIDTH, TedConstants.EDITOR_TAB_HEIGHT),
                                             editor, this::onClickTab);
            tabs.add(newTab);
            addElement(newTab);
            x += TedConstants.EDITOR_TAB_WIDTH;
        }
    }

    private void onClickTab(ClickableWidget clickableWidget, int button) {
        if (!(clickableWidget instanceof EditorTab tab)) {
            return;
        }

        setActiveEditor(tab.getEditor());
    }

    private void onClickLineStarter(ClickableWidget clickableWidget, int button) {
        if (!(clickableWidget instanceof TemplateLineStarterButton lsButton)) {
            return;
        }

        for (NewTemplateEditor editor : editors) {
            if (editor.getLineStarter().equals(lsButton.getLineStarter())) {
                setActiveEditor(editor);
                return;
            }
        }
        WeCode.TEMPLATE_EDITOR_HANDLER.addTemplateFromStarter(lsButton.getLineStarter());
    }

    private void updateLineStarters() {
        double y = TedConstants.EDITOR_Y;
        double x = 0;
        removeElements(buttons);
        buttons.clear();
        int i = 0;
        for (LineStarter lineStarter :  starters) {
            TemplateLineStarterButton button = new TemplateLineStarterButton(
                new DynamicPositioning(x, y,
                                       TedConstants.LINE_STARTER_WIDTH,
                                       TedConstants.LINE_STARTER_HEIGHT),
                this::onClickLineStarter, lineStarter
            );
            buttons.add(button);
            addElement(button);
            y += TedConstants.EDITOR_TAB_HEIGHT;
            i += 1;
        }
    }

    private void setActiveEditor(NewTemplateEditor editor) {
        setSelected(editor);
        currentEditorIndex = editors.indexOf(editor);
        updateEditors();
    }

    private List<LineStarter> starters = new ArrayList<>();
    private long nextCache = 0;
    private static final int CACHE_DELAY = 5000;
    private boolean awaitingCache = false;

    @Override
    public void tick() {
        if (nextCache < System.currentTimeMillis()) {
            nextCache = System.currentTimeMillis() + CACHE_DELAY;

            PlotDataManager.cacheLineStarters();
            awaitingCache = true;
            return;
        }
        if (awaitingCache && !PlotDataManager.isCurrentlyCachingLineStarters()) {
            starters = PlotDataManager
                .getLineStarters()
                .stream()
                .toList();
            updateLineStarters();
            awaitingCache = false;
        }
    }

    public NewTemplateScreen() {
        super(Text.literal("Teditor"));
        updateEditorTabs();
    }

    public void addTemplate(Template template, LineStarter lineStarter) {
        TemplateParser parser = new TemplateParser(template);
        addTemplateEditor(new NewTemplateEditor(
            lineStarter,
            parser.parse()
        ));
    }
}
