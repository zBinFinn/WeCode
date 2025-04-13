package org.zbinfinn.wecode.action_dump.sound;

import com.google.gson.JsonObject;

import java.util.List;

public record DumpSound(
    String sound,
    String soundID,
    String name,
    List<DumpSoundVariant> variants
) {
    public DumpSound(JsonObject sound) {
        this(
            sound.get("sound").getAsString(),
            sound.get("soundId").getAsString(),
            sound.getAsJsonObject("icon").get("name").getAsString(),
            !sound.has("variants") ? List.of() :
                sound.getAsJsonArray("variants")
                    .asList()
                    .stream()
                    .map(element -> new DumpSoundVariant(element.getAsJsonObject()))
                    .toList()
        );
    }
}
