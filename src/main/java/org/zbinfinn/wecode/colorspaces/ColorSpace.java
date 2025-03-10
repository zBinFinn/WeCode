package org.zbinfinn.wecode.colorspaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

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
}
