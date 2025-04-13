package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonObject;
import org.zbinfinn.wecode.action_dump.action.DumpActions;
import org.zbinfinn.wecode.action_dump.sound.DumpSounds;

public class ActionDump {
    private final JsonObject JSON;
    public final DumpActions actions;
    public final DumpSounds sounds;

    public ActionDump(JsonObject json) {
        this.JSON = json;
        actions = new DumpActions(JSON.get("actions").getAsJsonArray());
        sounds = new DumpSounds(JSON.get("sounds").getAsJsonArray());
    }
}
