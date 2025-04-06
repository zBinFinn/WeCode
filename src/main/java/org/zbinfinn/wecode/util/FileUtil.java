package org.zbinfinn.wecode.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.zbinfinn.wecode.WeCode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileUtil {
    public static File getConfigFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), WeCode.MOD_ID + ".json");
    }

    public static void writeConfig(String content) throws IOException {
        boolean ignore;
        File file = getConfigFile();
        Files.deleteIfExists(file.toPath());
        Files.createFile(file.toPath());
        if (!file.exists()) ignore = file.createNewFile();
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.WRITE);
    }

    public static String readConfig() throws IOException {
        return Files.readString(getConfigFile().toPath());
    }

    public static JsonObject loadJSONExternal(String folder, String fileName) {
        File file = new File(folder + File.separator + fileName);
        if (!file.exists()) {
            try {
                if (file.getParentFile() != null) {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                }
                file.createNewFile();
            } catch (IOException e) {
                WeCode.LOGGER.error("Failed to create {} empty file", fileName);
            }
            return new JsonObject();
        }
        try {
            String jsonStr = new String(Files.readAllBytes(file.toPath()));
            return JsonParser.parseString(jsonStr).getAsJsonObject();
        } catch (Exception e) {
            WeCode.LOGGER.error("Failed to load {} (invalid format?)", fileName);
        }
        return new JsonObject();
    }

    public static JsonObject loadJSON(String filename) {
        return loadJSONExternal("wecode", filename);
    }

    public static void saveJSON(String filename, JsonObject data) throws IOException {
        File file = new File("wecode" + File.separator + filename);

        FileWriter fileWriter = new FileWriter(file);

        WeCode.LOGGER.info("Saving: {}", filename);

        fileWriter.write(data.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}
