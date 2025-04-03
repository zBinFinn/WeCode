package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonObject;

public class ActionDump {
    private final JsonObject JSON;
    public final DumpActions actions;
    public ActionDump(JsonObject json) {
        this.JSON = json;
        actions = new DumpActions(JSON.get("actions").getAsJsonArray());
    }


}
