package org.zbinfinn.wecode.action_dump.action;

import com.google.gson.JsonObject;

public record DumpActionTagOption(String name) {
    public DumpActionTagOption(JsonObject json) {
        this(json.get("name").getAsString());
    }
}
