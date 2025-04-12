package org.zbinfinn.wecode.template_editor.refactor.rendering;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.template_editor.refactor.gui.TemplateLineStarterButton;
import org.zbinfinn.wecode.template_editor.refactor.rendering.traits.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WecodeScreen extends Screen {
    private Set<GUIElement> elements = new HashSet<>();
    private Set<Hoverable> currentlyHovered = new HashSet<>();
    private Selectable selected;

    protected WecodeScreen(Text title) {
        super(title);
    }

    public void addElement(GUIElement element) {
        elements.add(element);
    }

    public void removeElement(GUIElement element) {
        elements.remove(element);
    }

    public void removeElements(List<TemplateLineStarterButton> elements) {
        elements.forEach(this.elements::remove);
    }

    public void setSelected(Selectable selected) {
        if (this.selected != null) {
            this.selected.onDeselect();
        }
        this.selected = selected;
        this.selected.onSelect();
    }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
        for (GUIElement element : elements) {
            if (element instanceof Renderable renderable) {
                renderable.render(draw);
            }
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (GUIElement element : elements) {
            if (element instanceof Hoverable hoverable) {
                if (element.getPositioning().isMouseOver(mouseX, mouseY)) {
                    if (!currentlyHovered.contains(hoverable)) {
                        currentlyHovered.add(hoverable);
                        hoverable.onHover();
                    }
                } else {
                    if (currentlyHovered.contains(hoverable)) {
                        currentlyHovered.remove(hoverable);
                        hoverable.onUnhover();
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("Button: " + button);
        for (GUIElement element : elements.toArray(new GUIElement[0])) {
            if (element.getPositioning().isMouseOver(mouseX, mouseY)) {
                if (element instanceof Selectable selectable) {
                    setSelected(selectable);
                }
                if (element instanceof Clickable clickable) {
                    clickable.click(button, mouseX, mouseY);
                }
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        System.out.println(selected);
        if (selected != null && selected instanceof Typable typable) {
            typable.charTyped(chr, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selected != null && selected instanceof KeyPressable keyPressable) {
            if (keyPressable.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
