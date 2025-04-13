package org.zbinfinn.wecode.action_dump.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DumpSounds {
    private final JsonArray json;
    public final HashMap<String, DumpSound> soundsMap = new HashMap<>();
    public final List<DumpSound> sounds = new ArrayList<>();
    public DumpSounds(JsonArray json) {
        this.json = json;

        for (JsonElement jsonElement : json) {
            var obj = jsonElement.getAsJsonObject();
            DumpSound sound = new DumpSound(obj);
            sounds.add(sound);
            soundsMap.put(obj.getAsJsonObject("icon").get("name").getAsString(), sound);
            System.out.println(sounds.getLast());
        }
    }
}
