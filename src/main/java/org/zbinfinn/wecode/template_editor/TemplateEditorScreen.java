package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.templates.Arguments;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.templates.argument.StringArgument;
import dev.dfonline.flint.templates.builders.CodeBuilder;
import dev.dfonline.flint.templates.codeblock.PlayerAction;
import dev.dfonline.flint.templates.codeblock.target.PlayerTarget;
import dev.dfonline.flint.util.message.impl.CompoundMessage;
import dev.dfonline.flint.util.message.impl.prefix.ErrorMessage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private final ButtonWidget saveButton =
        new ButtonWidget.Builder(Text.literal("SAVE"), (this::saveButton))
            .dimensions(SAVE_BUTTON_X, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT)
            .tooltip(Tooltip.of(Text.literal("Save Template")))
            .build();

    private final ButtonWidget templateSwapper =
        new ButtonWidget.Builder(Text.literal("SWAP"), (this::swapTemplate))
            .dimensions(SAVE_BUTTON_X - SAVE_BUTTON_WIDTH - 4, SAVE_BUTTON_Y, SAVE_BUTTON_WIDTH, SAVE_BUTTON_HEIGHT)
            .tooltip(Tooltip.of(Text.literal("Swap Active Template")))
            .build();

    private final LineStarterDisplay lineStarterDisplay = new LineStarterDisplay(
        LINESTARTER_X, LINESTARTER_Y, LINESTARTER_WIDTH, LINESTARTER_HEIGHT, this::clickLineStarterDisplay
    );

    private void clickLineStarterDisplay(LineStarter lineStarter) {
        for (int i = 0; i < editorWindows.size(); i++) {
            TemplateEditor editor = editorWindows.get(i);
            if (editor.getName().equals(lineStarter.getName())) {
                setActiveTemplateEditor(i);
                return;
            }
        }

        WeCode.TEMPLATE_EDITOR_HANDLER.addTemplateItemFromLinestarter(lineStarter);
    }

    @Override
    protected void init() {
        clearChildren();

        saveButton.setNavigationOrder(1);
        addDrawableChild(lineStarterDisplay);
        addDrawableChild(saveButton);
        addDrawableChild(templateSwapper);
        setActiveTemplateEditor(currentTemplateEditorIndex);
        setFocused(currentEditor());
    }

    public void addTemplate(Template template) {
        editorWindows.add(newTemplateEditor(template, template.getName()));
        templates.add(template);
        setActiveTemplateEditor(templates.size() - 1);
    }

    private void swapTemplate(ButtonWidget buttonWidget) {
        var newIndex = currentTemplateEditorIndex + 1;
        if (newIndex >= editorWindows.size()) {
            newIndex = 0;
        }
        setActiveTemplateEditor(newIndex);
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
            TemplateTabButton button = new TemplateTabButton(currentX, currentY - HEIGHT, WIDTH, HEIGHT, Text.literal(editor.getName()), this::templateTabButton, i);
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

    private void saveButton(ButtonWidget buttonWidget) {
        // TODO:
        Tokenizer tokenizer = new Tokenizer(currentEditor().getText());
        var tokens = tokenizer.tokenize(false);

        System.out.println("Starting To Tokenize: ");
        for (var token : tokens) {
            System.out.println(token.debugString());
        }
        System.out.println("Ending To Tokenize: ");

        try {
            Parser parser = new Parser(tokens);
            Template template = parser.parse();

            Flint.getUser().getPlayer().giveItemStack(template.toItem());
        } catch (ParseException e) {
            Flint.getUser().sendMessage(new CompoundMessage(
                new ErrorMessage("Something went horribly wrong when parsing :(")
            ));
            e.printStackTrace();
        }

        close();
    }

    private TemplateEditor currentEditor() {
        return editorWindows.get(currentTemplateEditorIndex);
    }


    public TemplateEditorScreen() {
        super(Text.literal("Template Editor"));
        Arguments test = new Arguments();
        test.add(new StringArgument(0, "Hi"));
        Template template = new Template();
        template.setName("Template");
        template.setAuthor("Author");
        template.setBlocks(CodeBuilder
                               .create()
                               .add(new PlayerAction("SendMessage", PlayerTarget.NONE).setArguments(test))
                               .build());
        addTemplate(template);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private TemplateEditor newTemplateEditor(String name) {
        TemplateEditor out = new TemplateEditor(TEMPLATE_EDITOR_X, TEMPLATE_EDITOR_Y,
                                                TEMPLATE_EDITOR_WIDTH, TEMPLATE_EDITOR_HEIGHT);
        out.setName(name);
        return out;
    }

    private TemplateEditor newTemplateEditor(Template template, String name) {
        TemplateEditor out = newTemplateEditor(name);
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
        currentEditor().render(draw, mouseX, mouseY, delta);

        MatrixStack stack = draw.getMatrices();
        stack.push();
        stack.scale(2, 2, 2);
        // render here
        stack.pop();
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
