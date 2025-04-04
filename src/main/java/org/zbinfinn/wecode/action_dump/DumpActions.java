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
    private HashMap<String, HashMap<String, DumpAction>> groupsMaps = new HashMap<>();

    public DumpActions(JsonArray json) {
        this.JSON = json;
        for (JsonElement jsonElement : JSON) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (obj.get("icon").getAsJsonObject().get("name").getAsString().isEmpty()) {
                continue;
            }
            DumpAction action = new DumpAction(obj);
            System.out.println("Action: " + action.name());
            System.out.println(obj.get("name"));
            System.out.println(obj);
            actions.add(action);
            String group = action.block();
            if (!groups.containsKey(group)) {
                groups.put(group, new HashSet<>());
                groupsMaps.put(group, new HashMap<>());
            }
            groups.get(group).add(action.name());
            groupsMaps.get(group).put(action.name(), action);
            if (!action.name().equals(action.nameWithSpaces())) {
                System.out.println("Discrepancy: " + action);
            }
        }
    }

    public HashMap<String, Set<String>> getGroups() {
        return groups;
    }
    public HashMap<String, HashMap<String, DumpAction>> getGroupsMaps() {
        return groupsMaps;
    }

    public Set<DumpAction> getActions() {
        return actions;
    }
}
