package org.zbinfinn.wecode.templates;

import com.google.gson.JsonObject;
import org.zbinfinn.wecode.helpers.MessageHelper;

public class CodeBlock {
    private String id;
    private String block;

    public CodeBlock(String id, String block) {
        this.id = id;
        this.block = block;
    }

    public static CodeBlock fromJSON(JsonObject json) {
        return new CodeBlock(json.get("id").getAsString(), json.get("block").getAsString());
    }

    public String getBlock() {
        return block;
    }
}
