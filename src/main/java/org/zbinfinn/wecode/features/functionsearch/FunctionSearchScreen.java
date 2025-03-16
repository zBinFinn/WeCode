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
import java.util.List;

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

    private String searchTerm = "BIG BALLS";
    private int selectedIndex = 0;
    private int scrollIndex = 0;

    private int BACKGROUND_COLOR;

    private List<LineStarter> lineStartersToDisplay;
    private ArrayList<LineStarter> potentialLineStartersToDisplay;

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
        LINES_TO_SHOW = (int) ((BOX_HEIGHT - (searchBox == null ? 0 : searchBox.getHeight())) / Y_OFFSET);

        if (searchBox != null) {
            if (!searchTerm.equals(searchBox.getText())) {
                searchTerm = searchBox.getText();
                scrollIndex = 0;
                selectedIndex = 0;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollIndex -= (int) verticalAmount;
        selectedIndex += (int) horizontalAmount;
        if (scrollIndex + LINES_TO_SHOW > potentialLineStartersToDisplay.size()) {
            scrollIndex = potentialLineStartersToDisplay.size() - LINES_TO_SHOW;
        }
        if (scrollIndex < 0) {
            scrollIndex = 0;
        }
        applySelectedIndexChecks();
        return true;
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
            applySelectedIndexChecks();
            return false;
        }
        // Arrow Up
        if (keyCode == 265) {
            selectedIndex--;
            applySelectedIndexChecks();
            return false;
        }
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
            potentialLineStartersToDisplay = new ArrayList<>();
        }
        updateLineStarters();
        background(context);
        suggestedLineStarters(context);
    }

    private void updateLineStarters() {
        lineStartersToDisplay.clear();
        potentialLineStartersToDisplay.clear();
        for (LineStarter lineStarter : PlotDataManager.getLineStarters()) {
            if (!searchTerm.isEmpty() && !lineStarter.getName().toLowerCase().startsWith(searchTerm.toLowerCase())) {
                continue;
            }
            potentialLineStartersToDisplay.add(lineStarter);
        }
        int endIndex = LINES_TO_SHOW + scrollIndex;
        if (endIndex >= potentialLineStartersToDisplay.size()) {
            endIndex = potentialLineStartersToDisplay.size();
        }
        lineStartersToDisplay = potentialLineStartersToDisplay.subList(scrollIndex, endIndex);
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

    private void applySelectedIndexChecks() {
        if (selectedIndex >= lineStartersToDisplay.size()) {
            selectedIndex = lineStartersToDisplay.size() - 1;
        }
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }
    }
}
