package org.zbinfinn.wecode.colorspaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.ColorPalette;
import org.zbinfinn.wecode.helpers.MessageHelper;

import java.util.HashMap;
import java.util.Optional;

public class ColorSpace {
    private final HashMap<String, Color> colors = new HashMap<>();

    public static Optional<ColorSpace> fromJSON(String json) {
        try {
            return fromJSON(JsonParser.parseString(json));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<ColorSpace> fromJSON(JsonElement json) {
        try {
            ColorSpace cs = new ColorSpace();
            for (String cName : json.getAsJsonObject().keySet()) {
                String color = json.getAsJsonObject().get(cName).getAsString();
                cs.addColor(cName, color);
            }
            return Optional.of(cs);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void addColor(String name, Color color) {
        colors.put(name, color);
    }

    public void addColor(String name, String hex) {
        colors.put(name, new Color(hex));
    }

    public HashMap<String, Color> getColorMap() {
        return colors;
    }

    public String replaceAll(String content) {
        for (String key : colors.keySet()) {
            content = content.replaceAll("<<" + key + ">>", "<" + colors.get(key).toString() + ">");
        }
        return content;
    }

    public JsonObject toJSON() {
        JsonObject json = new JsonObject();
        for (String colorName : colors.keySet()) {
            json.addProperty(colorName, colors.get(colorName).toString());
        }
        return json;
    }

    public void print() {
        for (String cName : getColorMap().keySet().stream().sorted(String::compareToIgnoreCase).toList()) {
            Color c = getColorMap().get(cName);

            MessageHelper.messageIndent(
                    (ColorPalette.withColor(cName + ": ", org.zbinfinn.wecode.Color.LIGHT_PURPLE).copy().append(c.getColoredText()))
                            .styled(style ->
                                style.withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Text.literal("LC to copy | SHIFT-LC to insert").withColor(0x666666)
                                ))
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.COPY_TO_CLIPBOARD,
                                        "<" + c.toString() + ">"
                                ))
                                .withInsertion("<" + c.toString() + ">")
                            ), 3);
        }
    }
}
