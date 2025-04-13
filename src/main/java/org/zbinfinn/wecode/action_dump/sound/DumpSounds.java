package org.zbinfinn.wecode.action_dump.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class DumpSounds {
    private final JsonArray json;
    public final List<DumpSound> sounds = new ArrayList<>();
    public DumpSounds(JsonArray json) {
        this.json = json;

        for (JsonElement jsonElement : json) {
            var obj = jsonElement.getAsJsonObject();
            sounds.add(new DumpSound(obj));
            System.out.println(sounds.getLast());
        }
    }
}
