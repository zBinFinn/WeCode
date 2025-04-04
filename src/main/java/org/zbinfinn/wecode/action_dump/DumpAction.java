package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public record DumpAction(String name, String block, boolean legacy) implements Comparable<DumpAction> {
    public DumpAction(JsonObject object) {
        this(object.get("name").getAsString(),
             object.get("codeblockName").getAsString(),
             object.get("icon").getAsJsonObject().get("name").isJsonNull());
    }

    @Override
    public int compareTo(@NotNull DumpAction other) {
        return name.compareTo(other.name);
    }
}
