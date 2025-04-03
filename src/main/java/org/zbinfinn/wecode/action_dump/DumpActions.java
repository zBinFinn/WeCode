package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class DumpActions {
    private final JsonArray JSON;
    private Set<DumpAction> actions = new HashSet<>();
    public DumpActions(JsonArray json) {
        this.JSON = json;
        for (JsonElement jsonElement : JSON) {
            JsonObject obj = jsonElement.getAsJsonObject();
            actions.add(new DumpAction(obj));
        }
    }

    public Set<DumpAction> getActions() {
        return actions;
    }
}
