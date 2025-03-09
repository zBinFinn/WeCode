package org.zbinfinn.wecode.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.zbinfinn.wecode.templates.CodeBlock;
import org.zbinfinn.wecode.templates.Template;

import java.io.IOException;
import java.util.ArrayList;

public class TemplateUtil {
    public static Template fromItem(ItemStack item) {
        NbtComponent data = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = data.copyNbt();
        NbtCompound pbv = nbt.getCompound("PublicBukkitValues");
        String templateDataString = pbv.getString("hypercube:codetemplatedata");
        JsonObject templateData = JsonParser.parseString(templateDataString).getAsJsonObject();

        String name = templateData.get("name").getAsString();
        String author = templateData.get("author").getAsString();
        String version = templateData.get("version").getAsString();
        String encodedCode = templateData.get("code").getAsString();
        JsonObject code;
        try {
            byte[] decoded = CompressionUtil.fromGZIP(CompressionUtil.fromBase64(encodedCode.getBytes()));
            code = JsonParser.parseString(new String(decoded)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return fromTemplateData(code, author, name, version);
    }

    public static Template fromTemplateData(JsonObject code, String author, String name, String version) {
        JsonArray jsonBlocks = code.getAsJsonArray("blocks");
        ArrayList<CodeBlock> blocks = new ArrayList<>();
        for (JsonElement obj : jsonBlocks) {
            blocks.add(CodeBlock.fromJSON(obj.getAsJsonObject()));
        }
        return new Template(blocks, author, name, version);
    }
}
