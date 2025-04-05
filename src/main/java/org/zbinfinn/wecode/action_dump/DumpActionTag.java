package org.zbinfinn.wecode.action_dump;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DumpActionTag
    (String name, String defaultOption, int slot, List<DumpActionTagOption> options)
implements Comparable<DumpActionTag> {
    public DumpActionTag(JsonObject json) {
        this(json.get("name").getAsString(),
             json.get("defaultOption").getAsString(),
             json.get("slot").getAsInt(),
             json.get("options")
                 .getAsJsonArray()
                 .asList()
                 .stream()
                 .map(obj -> new DumpActionTagOption(obj.getAsJsonObject()))
                 .toList()
             );
    }

    @Override
    public int compareTo(@NotNull DumpActionTag other) {
        return Integer.compare(this.slot, other.slot);
    }
}
