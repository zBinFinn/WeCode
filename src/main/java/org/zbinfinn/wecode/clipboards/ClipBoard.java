package org.zbinfinn.wecode.clipboards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.ColorPalette;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.HashMap;
import java.util.Optional;

public class ClipBoard {
    private final HashMap<String, Value> colors = new HashMap<>();

    public void setValue(String valueName, Value newValue) {
        if (getColorMap().containsKey(valueName)) {
            NotificationHelper.sendAppliedNotification(Text.literal("Replaced value " + valueName + ": ").append(getColorMap().get(valueName).render()).append(Text.literal(" with ").append(newValue.render())), 5);
        } else {
            NotificationHelper.sendAppliedNotification(Text.literal("Added value: " + valueName + " with ").append(newValue.render()), 5);
        }
        addValue(valueName, newValue);
    }

    public static Optional<ClipBoard> fromJSON(String json) {
        try {
            return fromJSON(JsonParser.parseString(json));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<ClipBoard> fromJSON(JsonElement json) {
        try {
            ClipBoard cs = new ClipBoard();
            for (String cName : json.getAsJsonObject().keySet()) {
                String color = json.getAsJsonObject().get(cName).getAsString();
                cs.addValue(cName, color);
            }
            return Optional.of(cs);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void addValue(String name, Value value) {
        colors.put(name, value);
    }

    public void addValue(String name, String value) {
        if (value.matches("#[0-9a-fA-F]{6}")) {
            addValue(name, new Color(value));
            return;
        }
        addValue(name, new Literal(value));
    }

    public HashMap<String, Value> getColorMap() {
        return colors;
    }

    public String replaceAll(String content) {
        for (String key : colors.keySet()) {
            content = content.replaceAll("<<" + key + ">>", colors.get(key).value());
        }
        return content;
    }

    public JsonObject toJSON() {
        JsonObject json = new JsonObject();
        for (String colorName : colors.keySet()) {
            json.addProperty(colorName, colors.get(colorName).data());
        }
        return json;
    }

    public void print() {
        for (String cName : getColorMap().keySet().stream().sorted(String::compareToIgnoreCase).toList()) {
            Value val = getColorMap().get(cName);

            MessageHelper.messageIndent(
                    (ColorPalette.withColor(cName + ": ", org.zbinfinn.wecode.Color.LIGHT_PURPLE).copy().append(val.render()))
                            .styled(style ->
                                style.withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Text.literal("LC to copy | SHIFT-LC to insert").withColor(0x666666)
                                ))
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.COPY_TO_CLIPBOARD,
                                        val.value()
                                ))
                                .withInsertion(val.value())
                            ), 3);
        }
    }
}
