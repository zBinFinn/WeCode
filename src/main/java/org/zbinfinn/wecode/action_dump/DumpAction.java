package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public record DumpAction(String name, String block, String nameWithSpaces) implements Comparable<DumpAction> {
    public DumpAction(JsonObject object) {
        this(object.get("name").getAsString().trim(),
             object.get("codeblockName").getAsString(),
             object.get("name").getAsString());

    }

    @Override
    public int compareTo(@NotNull DumpAction other) {
        return name.compareTo(other.name);
    }
}
