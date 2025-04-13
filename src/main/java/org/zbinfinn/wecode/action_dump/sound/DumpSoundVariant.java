package org.zbinfinn.wecode.action_dump.sound;

import com.google.gson.JsonObject;

public record DumpSoundVariant(String id, String name, int seed) {
    public DumpSoundVariant(JsonObject json) {
        this(json.get("id").getAsString(), json.get("name").getAsString(), json.get("seed").getAsInt());
    }
}
