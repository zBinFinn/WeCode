package org.zbinfinn.wecode.action_dump.action;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DumpAction(String name, String block, String nameWithSpaces, List<DumpActionTag> tags) implements Comparable<DumpAction> {
    public DumpAction(JsonObject object) {
        this(object.get("name").getAsString().trim(),
             object.get("codeblockName").getAsString(),
             object.get("name").getAsString(),
             object.get("tags")
                 .getAsJsonArray()
                 .asList()
                 .stream()
                 .map((obj) -> new DumpActionTag(obj.getAsJsonObject()))
                 .sorted()
                 .toList()
        );
    }

    @Override
    public int compareTo(@NotNull DumpAction other) {
        return name.compareTo(other.name);
    }
}
