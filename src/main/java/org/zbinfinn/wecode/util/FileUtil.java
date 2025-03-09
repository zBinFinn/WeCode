package org.zbinfinn.wecode.util;

import net.fabricmc.loader.api.FabricLoader;
import org.zbinfinn.wecode.WeCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

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
}
