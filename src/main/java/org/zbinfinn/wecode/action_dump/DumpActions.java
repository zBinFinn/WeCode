package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DumpActions {
    private final JsonArray JSON;
    private Set<DumpAction> actions = new HashSet<>();

    private HashMap<String, Set<String>> groups = new HashMap<>();

    public DumpActions(JsonArray json) {
        this.JSON = json;
        for (JsonElement jsonElement : JSON) {
            JsonObject obj = jsonElement.getAsJsonObject();
            DumpAction action = new DumpAction(obj);
            actions.add(action);
            String group = action.block();
            if (!groups.containsKey(group)) {
                groups.put(group, new HashSet<>());
            }
            groups.get(group).add(action.name());
        }
    }

    public HashMap<String, Set<String>> getGroups() {
        return groups;
    }
    public Set<DumpAction> getActions() {
        return actions;
    }
}
