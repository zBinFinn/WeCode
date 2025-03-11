package org.zbinfinn.wecode.features.functionsearch;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.DFColors;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.PlotDataManager;
import org.zbinfinn.wecode.helpers.MessageHelper;

import java.util.ArrayList;

public class FunctionSearchScreen extends Screen {
    private int BOX_WIDTH;
    private int BOX_HEIGHT;
    private int LEFT_X;
    private int RIGHT_X;
    private int BOTTOM_Y;
    private int TOP_Y;
    private int Y_OFFSET;
    private int LINES_TO_SHOW;

    private EditBoxWidget searchBox;

    private String searchTerm = "";
    private int selectedIndex = 0;

    private int BACKGROUND_COLOR;

    private ArrayList<LineStarter> lineStartersToDisplay;

    public FunctionSearchScreen() {
        super(Text.literal("Function Search"));
    }

    @Override
    protected void init() {
        updateVars();
        searchBox = new FunctionSearchBox(textRenderer, LEFT_X, TOP_Y, BOX_WIDTH, textRenderer.fontHeight * 2);
        addDrawable(searchBox);
        setInitialFocus(searchBox);
    }

    private void updateVars() {
        BOX_WIDTH = 240;
        BOX_HEIGHT = (int) (height * 0.6);

        LEFT_X = width / 2 - BOX_WIDTH / 2;
        RIGHT_X = width / 2 + BOX_WIDTH / 2;
        BOTTOM_Y = height / 2 + BOX_HEIGHT / 2;
        TOP_Y = height / 2 - BOX_HEIGHT / 2;

        BACKGROUND_COLOR = 0x88_000000;

        Y_OFFSET = textRenderer.fontHeight + 2;
        LINES_TO_SHOW = (int) ((BOX_HEIGHT - (searchBox == null ? 0 : searchBox.getHeight())) / Y_OFFSET) - 1;

        if (searchBox != null) {
            searchTerm = searchBox.getText();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (selectedIndex >= lineStartersToDisplay.size() || selectedIndex < 0 ) {
                return false;
            }
            LineStarter lineStarter = lineStartersToDisplay.get(selectedIndex);
            if (lineStarter == null) {
                return false;
            }

            close();
            CommandSender.queue("ctp " + lineStarter.getType() + " " + lineStarter.getName());

            return false;
        }
        // Arrow Down
        if (keyCode == 264 || keyCode == InputUtil.GLFW_KEY_TAB) {
            selectedIndex++;
            if (selectedIndex >= lineStartersToDisplay.size()) {
                selectedIndex = 0;
            }
            return false;
        }
        // Arrow Up
        if (keyCode == 265) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = lineStartersToDisplay.size() - 1;
            }
            return false;
        }
        selectedIndex = 0;
        updateVars();
        updateLineStarters();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void blur() {
        return;
    }

    @Override
    protected void applyBlur() {
        return;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        updateVars();
        if (lineStartersToDisplay == null) {
            lineStartersToDisplay = new ArrayList<>();
        }
        updateLineStarters();
        background(context);
        suggestedLineStarters(context);
    }

    private void updateLineStarters() {
        lineStartersToDisplay.clear();
        int count = 0;
        for (LineStarter lineStarter : PlotDataManager.getLineStarters()) {
            if (count > LINES_TO_SHOW) {
                break;
            }
            if (!searchTerm.isEmpty() && !lineStarter.getName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                continue;
            }
            lineStartersToDisplay.add(lineStarter);
            count++;
        }
    }

    private void suggestedLineStarters(DrawContext context) {
        int currentY = TOP_Y + Y_OFFSET * 2;
        int currentX = LEFT_X + 4;

        int i = 0;
        for (LineStarter lineStarter : lineStartersToDisplay) {
            context.drawText(textRenderer, lineStarter.getPrefix().copy()
                            .append(Text.literal(" "))
                            .append(getLineStarterNameText(searchTerm, lineStarter.getName(), i)),
                    currentX, currentY, 0xFF_FFFFFF, true);
                    currentY += Y_OFFSET;
            i++;
        }

    }

    private Text getLineStarterNameText(String searchTerm, String name, int index) {
        return Text.literal(name.substring(0, searchTerm.length())).withColor(DFColors.YELLOW_LIGHT_2.color)
                .append(Text.literal(name.substring(searchTerm.length())).styled(style -> {
                    if (index == selectedIndex) {
                        return style.withColor(0xFFFFFF);
                    }
                    return style.withColor(0xCCCCCC);
                })).styled(style -> {
                    if (index == selectedIndex) {
                        return style.withUnderline(true);
                    }
                    return style;
                });
    }

    private void background(DrawContext context) {
        context.fill(LEFT_X, BOTTOM_Y, RIGHT_X, TOP_Y, BACKGROUND_COLOR);
    }
}
