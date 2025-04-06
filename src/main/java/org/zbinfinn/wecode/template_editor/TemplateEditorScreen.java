package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.templates.Template;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;

import java.util.ArrayList;
import java.util.List;

public class TemplateEditorScreen extends Screen {
    private final int SCREEN_WIDTH = Flint.getClient().getWindow().getScaledWidth();
    private final int SCREEN_HEIGHT = Flint.getClient().getWindow().getScaledHeight();

    private final int LINESTARTER_PADDING_BOTTOM = 30;
    private final int LINESTARTER_X = 10;
    private final int LINESTARTER_Y = 30;
    private final int LINESTARTER_WIDTH = 80;
    private final int LINESTARTER_HEIGHT = SCREEN_HEIGHT - LINESTARTER_Y - LINESTARTER_PADDING_BOTTOM;

    private final int TEMPLATE_EDITOR_PADDING_DOWN = 30;
    private final int TEMPLATE_EDITOR_X = 10 + LINESTARTER_WIDTH;
    private final int TEMPLATE_EDITOR_Y = 30;
    private final int TEMPLATE_EDITOR_RIGHT_PADDING = 10;
    private final int TEMPLATE_EDITOR_WIDTH = SCREEN_WIDTH - TEMPLATE_EDITOR_X - TEMPLATE_EDITOR_RIGHT_PADDING;
    private final int TEMPLATE_EDITOR_HEIGHT = SCREEN_HEIGHT - TEMPLATE_EDITOR_Y - TEMPLATE_EDITOR_PADDING_DOWN;

    private final int SAVE_BUTTON_HEIGHT = 20;
    private final int SAVE_BUTTON_WIDTH = 60;
    private final int SAVE_BUTTON_X = TEMPLATE_EDITOR_X + TEMPLATE_EDITOR_WIDTH + 2 - SAVE_BUTTON_WIDTH;
    private final int SAVE_BUTTON_Y = TEMPLATE_EDITOR_Y + TEMPLATE_EDITOR_HEIGHT + 5; //+ SAVE_BUTTON_HEIGHT;

    private final List<TemplateEditor> editorWindows = new ArrayList<>();
    private final List<Template> templates = new ArrayList<>();
    private final List<TemplateTabButton> tabButtons = new ArrayList<>();
    private int currentTemplateEditorIndex = 0;
    private boolean updateActiveTemplateEditor = false;

    private final ButtonWidget saveAllButton =
        new ButtonWidget.Builder(Text.literal("SAVE ALL & EXIT"), (this::saveAllAndExit))
            .dimensions(SAVE_BUTTON_X, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT)
            .tooltip(Tooltip.of(Text.literal("Save Templates")))
            .build();

    private final ButtonWidget saveButton =
        new ButtonWidget.Builder(Text.literal("SAVE"), (this::saveCurrent))
            .dimensions(SAVE_BUTTON_X - SAVE_BUTTON_WIDTH - 4, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT)
            .tooltip(Tooltip.of(Text.literal("Save just this template")))
            .build();

    private final LineStarterDisplay lineStarterDisplay = new LineStarterDisplay(
        LINESTARTER_X, LINESTARTER_Y, LINESTARTER_WIDTH, LINESTARTER_HEIGHT, this::clickLineStarterDisplay
    );

    private void clickLineStarterDisplay(LineStarter lineStarter) {
        System.out.println("Comparing " + lineStarter + " to: ");
        for (int i = 0; i < editorWindows.size(); i++) {
            TemplateEditor editor = editorWindows.get(i);
            System.out.println(editor.getLineStarter());
            if (editor.getLineStarter().equals(lineStarter)) {
                setActiveTemplateEditor(i);
                return;
            }
        }

        WeCode.TEMPLATE_EDITOR_HANDLER.addTemplateItemFromLinestarter(lineStarter);
    }

    @Override
    protected void init() {
        clearChildren();

        saveAllButton.setNavigationOrder(1);
        addDrawableChild(saveButton);
        addDrawableChild(saveAllButton);
        addDrawableChild(lineStarterDisplay);
    }

    public void addTemplate(Template template, LineStarter lineStarter) {
        editorWindows.add(newTemplateEditor(template, lineStarter));
        templates.add(template);
        setActiveTemplateEditor(templates.size() - 1);
    }

    private void saveCurrent(ButtonWidget buttonWidget) {
        WeCode.TEMPLATE_EDITOR_HANDLER.saveTemplate(currentEditor());
    }

    private void saveAllAndExit(ButtonWidget buttonWidget) {
        // TODO:
        Template template = currentEditor().getTemplate();

        if (template != null) {
            Flint.getUser().getPlayer().giveItemStack(template.toItem());
        }
        close();
    }

    private void setActiveTemplateEditor(int index) {
        currentEditor().setVisible(false);
        remove(currentEditor());

        currentTemplateEditorIndex = index;

        addDrawableChild(currentEditor());

        currentEditor().setVisible(true);
        updateActiveTemplateEditor = true;

        updateTemplateTabs();

        System.out.println("New Index: " + index);
    }

    private void updateTemplateTabs() {
        for (TemplateTabButton button : tabButtons) {
            remove(button);
        }
        tabButtons.clear();
        int currentX = currentEditor().getX();
        int currentY = currentEditor().getY();

        final int WIDTH = 60;
        final int HEIGHT = 15;

        for (int i = 0; i < editorWindows.size(); i++) {
            TemplateEditor editor = editorWindows.get(i);
            TemplateTabButton button = new TemplateTabButton(currentX, currentY - HEIGHT, WIDTH, HEIGHT, Text.literal(editor.getLineStarter().getName()), this::templateTabButton, i);
            if (i == currentTemplateEditorIndex) {
                button.setSelected(true);
            }
            tabButtons.add(button);
            addDrawableChild(button);
            currentX += WIDTH;
        }
    }

    private void templateTabButton(ButtonWidget buttonWidget) {
        if (buttonWidget instanceof TemplateTabButton tabButton) {
            System.out.println("Clicked Tab: " + tabButton.getId());
            setActiveTemplateEditor(tabButton.getId());
        }
    }

    private @Nullable TemplateEditor currentEditor() {
        if (editorWindows.isEmpty()) {
            return null;
        }
        return editorWindows.get(currentTemplateEditorIndex);
    }


    public TemplateEditorScreen() {
        super(Text.literal("Template Editor"));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private TemplateEditor newTemplateEditor(LineStarter lineStarter) {
        TemplateEditor out = new TemplateEditor(TEMPLATE_EDITOR_X, TEMPLATE_EDITOR_Y,
                                                TEMPLATE_EDITOR_WIDTH, TEMPLATE_EDITOR_HEIGHT);
        out.setLineStarter(lineStarter);
        return out;
    }

    private TemplateEditor newTemplateEditor(Template template, LineStarter lineStarter) {
        TemplateEditor out = newTemplateEditor(lineStarter);
        TemplateParser parser = new TemplateParser(template);
        out.setText(parser.parse());
        return out;
    }

    @Override
    public void tick() {
        if (updateActiveTemplateEditor) {
            updateActiveTemplateEditor = false;
            setFocused(currentEditor());
        }
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
        if (currentEditor() != null) {
            currentEditor().render(draw, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setCaching(boolean caching) {
        lineStarterDisplay.setCaching(caching);
    }
}
