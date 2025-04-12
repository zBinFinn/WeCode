package org.zbinfinn.wecode.template_editor.refactor.gui;

import dev.dfonline.flint.templates.Template;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.PlotDataManager;
import org.zbinfinn.wecode.template_editor.TemplateParser;
import org.zbinfinn.wecode.template_editor.refactor.TedConstants;
import org.zbinfinn.wecode.template_editor.refactor.rendering.WecodeScreen;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.FixedPositioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.positioning.Positioning;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.ClickableWidget;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.impl.GenericButton;

import java.util.ArrayList;
import java.util.List;

public class NewTemplateScreen extends WecodeScreen {
    private int currentEditorIndex = 0;
    private final List<NewTemplateEditor> editors = new ArrayList<>();
    private final List<EditorTab> tabs = new ArrayList<>();
    private final List<TemplateLineStarterButton> buttons = new ArrayList<>();

    private final GenericButton abortButton;

    private void abortButton(ClickableWidget clickableWidget, int mouse) {
        close();
        WeCode.TEMPLATE_EDITOR_HANDLER.reset();
    }

    public NewTemplateScreen() {
        super(Text.literal("Teditor"));
        updateEditorTabs();
        abortButton = new GenericButton(
            getAbortButtonPositioning(),
            this::abortButton,
            Text.literal("ABORT")
        );
        addElement(abortButton);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        updatePositions();
        System.out.println("Resize");
    }

    private void updatePositions() {
        updateEditors();
        updateLineStarters();
        updateEditorTabs();
        updateButtons();
    }

    private void updateButtons() {
        abortButton.setPos(getAbortButtonPositioning());
    }

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
                editor.setPos(genEditorPos());
                addElement(editor);
            }
        }
        updateEditorTabs();
    }

    private Positioning genEditorPos() {
        return new FixedPositioning(
            TedConstants.Dimensions.editorX(),
            TedConstants.Dimensions.editorY(),
            TedConstants.Dimensions.editorWidth(),
            TedConstants.Dimensions.editorHeight()
        );
    }

    private Positioning getAbortButtonPositioning() {
        return new FixedPositioning(
            TedConstants.Dimensions.editorX() + TedConstants.Dimensions.editorWidth() - 75,
            TedConstants.Dimensions.editorY() + TedConstants.Dimensions.editorHeight() + 2,
            75,
            20
        );
    }

    private void updateEditorTabs() {
        if (editors.isEmpty()) {
            return;
        }
        for (EditorTab button : tabs) {
            removeElement(button);
        }
        tabs.clear();
        int x = currentEditor().getPositioning().getX();
        int y = currentEditor().getPositioning().getY();
        for (NewTemplateEditor editor : editors) {
            EditorTab newTab = new EditorTab(new FixedPositioning(x, y - TedConstants.Dimensions.editorTabHeight(), TedConstants.Dimensions.editorTabWidth(), TedConstants.Dimensions.editorTabHeight()),
                                             editor, this::onClickTab);
            tabs.add(newTab);
            addElement(newTab);
            x += TedConstants.Dimensions.editorTabWidth();
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
        int x = TedConstants.Dimensions.lineStarterPaddingLeft();
        int y = TedConstants.Dimensions.editorY();
        removeElements(buttons);
        buttons.clear();
        for (LineStarter lineStarter :  starters) {
            TemplateLineStarterButton button = new TemplateLineStarterButton(
                new FixedPositioning(x, y,
                                     TedConstants.Dimensions.lineStarterWidth(),
                                     TedConstants.Dimensions.lineStarterHeight()),
                this::onClickLineStarter, lineStarter
            );
            buttons.add(button);
            addElement(button);
            y += TedConstants.Dimensions.editorTabHeight();
        }
    }

    private void setActiveEditor(NewTemplateEditor editor) {
        setSelected(editor);
        currentEditorIndex = editors.indexOf(editor);
        updateEditors();
    }

    private List<LineStarter> starters = new ArrayList<>();
    private long nextCache = 0;
    private static final int CACHE_DELAY = 25000;
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

    public void addTemplate(Template template, LineStarter lineStarter) {
        TemplateParser parser = new TemplateParser(template);
        addTemplateEditor(new NewTemplateEditor(
            lineStarter,
            parser.parse()
        ));
    }
}
