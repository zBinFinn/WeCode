package org.zbinfinn.wecode.clipboards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.util.FileUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;

public class ClipBoards {
    private static HashMap<String, ClipBoard> boards;
    private static String activeBoard = "";

    public static @Nullable ClipBoard getBoard(String clipboard) {
        if (!colorspaceExists(clipboard)) {
            thatClipBoardDoesNotExist(clipboard);
            return null;
        }
        return boards.get(clipboard);
    }

    public static void createBoard(String name) {
        if (boards.containsKey(name)) {
            NotificationHelper.sendFailNotification("That Colorspace already exists", 5);
            return;
        }

        ClipBoard clipBoard = new ClipBoard();
        clipBoard.addValue("default", "#88FF88");
        boards.put(name, clipBoard);

        NotificationHelper.sendAppliedNotification("Created Colorspace: '" + name + "'", 5);
    }

    public static void deleteBoard(String boardName) {
        if (!colorspaceExists(boardName)) {
            thatClipBoardDoesNotExist(boardName);
            return;
        }

        boards.remove(boardName);
        NotificationHelper.sendAppliedNotification("Deleted Colorspace: '" + boardName + "'", 5);
    }

    public static HashMap<String, ClipBoard> getBoards() {
        return boards;
    }

    public static void init() {
        loadBoards();
        try {
            save();
        } catch (IOException e) {
            WeCode.LOGGER.error("Failed to do initial safety save");
        }
    }

    public static void save() throws IOException {
        JsonObject save = new JsonObject();
        JsonObject colorspaces = new JsonObject();
        for (String key : boards.keySet()) {
            ClipBoard clipBoard = boards.get(key);
            JsonObject jsonObject = clipBoard.toJSON();
            colorspaces.add(key, jsonObject);
        }
        save.add("colorspaces", colorspaces);
        save.addProperty("active", activeBoard);

        FileUtil.saveJSON("colorspaces.json", save);
    }

    private static void loadBoards() {
        HashMap<String, ClipBoard> loadedBoards;
        try {
            JsonObject json = FileUtil.loadJSON("colorspaces.json");
            if (json.isEmpty()) {
                loadedBoards = getDefaultHashMap();
            } else {
                JsonElement boards = json.get("colorspaces");
                activeBoard = json.get("active").getAsString();
                loadedBoards = ClipBoards.fromJSON(boards.getAsJsonObject());
            }
        } catch (Exception e) {
            loadedBoards = getDefaultHashMap();
        }
        ClipBoards.boards = loadedBoards;
    }

    private static HashMap<String, ClipBoard> fromJSON(JsonElement json) {
        HashMap<String, ClipBoard> map = new HashMap<>();
        for (String cbName : json.getAsJsonObject().keySet()) {
            JsonElement cbElement = json.getAsJsonObject().get(cbName);
            Optional<ClipBoard> cbOptional = ClipBoard.fromJSON(cbElement);
            if (cbOptional.isEmpty()) {
                WeCode.LOGGER.warn("Failed to load colorspace '" + cbName + "'");
                continue;
            }
            ClipBoard cb = cbOptional.get();
            map.put(cbName, cb);
        }

        return map;
    }

    private static HashMap<String, ClipBoard> getDefaultHashMap() {
        HashMap<String, ClipBoard> defaultMap = new HashMap<>();
        defaultMap.put("global", getDefaultClipBoard());
        return defaultMap;
    }

    private static ClipBoard getDefaultClipBoard() {
        ClipBoard defaultClipBoard = new ClipBoard();
        defaultClipBoard.addValue("example", "#88FF88");
        return defaultClipBoard;
    }

    public static void addLiteral(String clipboardName, String valueName, String value) {
        if (!colorspaceExists(clipboardName)) {
            thatClipBoardDoesNotExist(clipboardName);
            return;
        }

        ClipBoard clipBoard = boards.get(clipboardName);
        Literal newValue = new Literal(value);

        clipBoard.setValue(clipboardName, newValue);
    }

    public static String getActiveBoard() {
        return activeBoard;
    }

    public static void setActiveBoard(String activeClipboard) {
        if (!boards.containsKey(activeClipboard)) {
            thatClipBoardDoesNotExist(activeClipboard);
            return;
        }
        ClipBoards.activeBoard = activeClipboard;
        NotificationHelper.sendAppliedNotification("Active Clipboard: " + activeClipboard, 3);
    }

    public static void removeValue(String clipboardName, String valueName) {
        if (!colorspaceExists(clipboardName)) {
            thatClipBoardDoesNotExist(clipboardName);
            return;
        }

        ClipBoard clipBoard = boards.get(clipboardName);
        if (!clipBoard.getColorMap().containsKey(valueName)) {
            thatColorDoesNotExist(clipboardName, valueName);
        }

        NotificationHelper.sendAppliedNotification(Text.literal("Removed value: " + valueName + ": ").append(clipBoard.getColorMap().get(valueName).render()).append(Text.literal(" from " + clipboardName)), 3);
        clipBoard.getColorMap().remove(valueName);
    }

    private static void thatColorDoesNotExist(String colorSpace, String colorName) {
        NotificationHelper.sendFailNotification("The color '" + colorName + "' does not exist in colorspace '" + colorSpace + "'", 5);
    }

    private static void thatClipBoardDoesNotExist(String colorspaceName) {
        NotificationHelper.sendFailNotification("Colorspace: " + colorspaceName + " does not exist", 5);
    }

    private static boolean colorspaceExists(String colorspaceName) {
        return boards.containsKey(colorspaceName);
    }

    public static String replaceAll(String content) {
        if (colorspaceExists(activeBoard)) {
            ClipBoard clipBoard = boards.get(activeBoard);
            content = clipBoard.replaceAll(content);
            if (activeBoard.equals("global")) {
                return content;
            }
        }

        if (boards.containsKey("global")) {
            ClipBoard globalSpace = boards.get("global");
            content = globalSpace.replaceAll(content);
        }

        return content;
    }

    public static void addValue(String clipboardName, String valueName, String value) {
        if (!colorspaceExists(clipboardName)) {
            thatClipBoardDoesNotExist(clipboardName);
            return;
        }

        ClipBoard clipBoard = boards.get(clipboardName);
        clipBoard.addValue(valueName, value);
    }
}
