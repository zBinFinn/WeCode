package org.zbinfinn.wecode.colorspaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ColorSpaces {
    private static HashMap<String, ColorSpace> spaces;
    private static String activeSpace = "";

    public static @Nullable ColorSpace getSpace(String colorspace) {
        if (!colorspaceExists(colorspace)) {
            thatColorSpaceDoesNotExist(colorspace);
            return null;
        }
        return spaces.get(colorspace);
    }

    public static void createSpace(String name) {
        if (spaces.containsKey(name)) {
            NotificationHelper.sendFailNotification("That Colorspace already exists", 5);
            return;
        }

        ColorSpace colorSpace = new ColorSpace();
        colorSpace.addColor("default", "#88FF88");
        spaces.put(name, colorSpace);

        NotificationHelper.sendAppliedNotification("Created Colorspace: '" + name + "'", 5);
    }

    public static void deleteSpace(String colorspaceName) {
        if (!colorspaceExists(colorspaceName)) {
            thatColorSpaceDoesNotExist(colorspaceName);
            return;
        }

        spaces.remove(colorspaceName);
        NotificationHelper.sendAppliedNotification("Deleted Colorspace: '" + colorspaceName + "'", 5);
    }

    public static HashMap<String, ColorSpace> getSpaces() {
        return spaces;
    }

    public static void init() {
        spaces = loadSpaces();
        try {
            save();
        } catch (IOException e) {
            WeCode.LOGGER.error("Failed to do initial safety save");
        }
    }

    private static HashMap<String, ColorSpace> loadSpaces() {
        File file = new File("wecode\\colorspaces.json");
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                WeCode.LOGGER.error("Failed to create colorspaces empty file");
            }
            WeCode.LOGGER.info("Returning default map");
            return getDefaultHashMap();
        }
        try {
            String jsonStr = new Scanner(file).tokens().collect(Collectors.joining());
            JsonElement save = JsonParser.parseString(jsonStr);
            JsonElement colorspaces = save.getAsJsonObject().get("colorspaces");
            activeSpace = save.getAsJsonObject().get("active").getAsString();

            return ColorSpaces.fromJSON(colorspaces);
        } catch (Exception e) {
            WeCode.LOGGER.error("Failed to load colorspaces.json (invalid format?)");
        }

        return getDefaultHashMap();
    }

    private static HashMap<String, ColorSpace> fromJSON(JsonElement json) {
        HashMap<String, ColorSpace> map = new HashMap<>();
        for (String csName : json.getAsJsonObject().keySet()) {
            JsonElement csElement = json.getAsJsonObject().get(csName);
            Optional<ColorSpace> csOpt = ColorSpace.fromJSON(csElement);
            if (csOpt.isEmpty()) {
                WeCode.LOGGER.warn("Failed to load colorspace '" + csName + "'");
                continue;
            }
            ColorSpace cs = csOpt.get();
            map.put(csName, cs);
        }

        return map;
    }

    private static HashMap<String, ColorSpace> getDefaultHashMap() {
        HashMap<String, ColorSpace> defaultMap = new HashMap<>();
        defaultMap.put("global", getDefaultColorSpace());
        for (String key : defaultMap.keySet()) {
            WeCode.LOGGER.info("{}: {}", key, defaultMap.get(key));
        }
        return defaultMap;
    }

    private static ColorSpace getDefaultColorSpace() {
        ColorSpace defaultColorSpace = new ColorSpace();
        defaultColorSpace.addColor("example", "#88FF88");
        return defaultColorSpace;
    }

    public static void save() throws IOException {
        JsonObject save = new JsonObject();
        JsonObject colorspaces = new JsonObject();
        for (String key : spaces.keySet()) {
            ColorSpace colorSpace = spaces.get(key);
            JsonObject jsonObject = colorSpace.toJSON();
            colorspaces.add(key, jsonObject);
        }
        save.add("colorspaces", colorspaces);
        save.addProperty("active", activeSpace);

        File file = new File("wecode\\colorspaces.json");

        FileWriter fileWriter = new FileWriter(file);

        WeCode.LOGGER.info("Saving Colorspaces to colorspaces.json");

        fileWriter.write(save.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static void addColor(String colorspaceName, String colorName, String color) {
        if (!colorspaceExists(colorspaceName)) {
            thatColorSpaceDoesNotExist(colorspaceName);
            return;
        }

        ColorSpace colorSpace = spaces.get(colorspaceName);
        Color newColor = new Color(color);

        if (colorSpace.getColorMap().containsKey(colorName)) {
            NotificationHelper.sendAppliedNotification(Text.literal("Replaced color " + colorName + ": ").append(colorSpace.getColorMap().get(colorName).getColoredText()).append(Text.literal(" with ").append(newColor.getColoredText())), 5);
        } else {
            NotificationHelper.sendAppliedNotification(Text.literal("Added color: " + colorName + " with ").append(newColor.getColoredText()), 5);
        }
        colorSpace.addColor(colorName, newColor);
    }

    public static String getActiveSpace() {
        return activeSpace;
    }

    public static void setActiveSpace(String activeSpace) {
        if (!spaces.containsKey(activeSpace)) {
            thatColorSpaceDoesNotExist(activeSpace);
            return;
        }
        ColorSpaces.activeSpace = activeSpace;
        NotificationHelper.sendAppliedNotification("Active Colorspace: " + activeSpace, 3);
    }

    public static void removeColor(String colorspaceName, String colorName) {
        if (!colorspaceExists(colorspaceName)) {
            thatColorSpaceDoesNotExist(colorspaceName);
            return;
        }

        ColorSpace colorSpace = spaces.get(colorspaceName);
        if (!colorSpace.getColorMap().containsKey(colorName)) {
            thatColorDoesNotExist(colorspaceName, colorName);
        }

        colorSpace.getColorMap().remove(colorName);
        NotificationHelper.sendAppliedNotification("Removed color: " + colorName + " from " + colorspaceName, 3);
    }

    private static void thatColorDoesNotExist(String colorSpace, String colorName) {
        NotificationHelper.sendFailNotification("The color '" + colorName + "' does not exist in colorspace '" + colorSpace + "'", 5);
    }

    private static void thatColorSpaceDoesNotExist(String colorspaceName) {
        NotificationHelper.sendFailNotification("Colorspace: " + colorspaceName + " does not exist", 5);
    }

    private static boolean colorspaceExists(String colorspaceName) {
        return spaces.containsKey(colorspaceName);
    }

    public static String replaceAll(String content) {
        if (colorspaceExists(activeSpace)) {
            ColorSpace colorSpace = spaces.get(activeSpace);
            content = colorSpace.replaceAll(content);
            if (activeSpace.equals("global")) {
                return content;
            }
        }

        if (spaces.containsKey("global")) {
            ColorSpace globalSpace = spaces.get("global");
            content = globalSpace.replaceAll(content);
        }

        return content;
    }
}
