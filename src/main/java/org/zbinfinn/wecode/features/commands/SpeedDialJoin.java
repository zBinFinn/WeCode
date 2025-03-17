package org.zbinfinn.wecode.features.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.Color;
import org.zbinfinn.wecode.ColorPalette;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.util.FileUtil;

import java.io.IOException;
import java.util.HashMap;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SpeedDialJoin extends CommandFeature {
    private HashMap<String, String> dials = new HashMap<>();

    @Override
    public void activate() {
        JsonObject data = FileUtil.loadJSON("speed_dial_join.json");
        if (data.isEmpty() || !data.has("dials")) {
            dials = new HashMap<>();
        } else {
            JsonObject dialsJson = data.getAsJsonObject("dials");
            for (String key : dialsJson.keySet()) {
                dials.put(key, dialsJson.get(key).getAsString());
            }
        }
        try {
            save();
        } catch (IOException e) {
            WeCode.LOGGER.error("Failed to sanity-save speed dial joins");
        }
    }

    public void save() throws IOException {
        JsonObject data = new JsonObject();
        JsonObject dialsJson = new JsonObject();
        for (String key : dials.keySet()) {
            dialsJson.addProperty(key, dials.get(key));
        }
        data.add("dials", dialsJson);
        FileUtil.saveJSON("speed_dial_join.json", data);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("sjoin")
                        .then(argument("name", StringArgumentType.string())
                                .suggests(((commandContext, suggestionsBuilder) -> {
                                    for (String key : dials.keySet()) {
                                        if (key.startsWith(suggestionsBuilder.getRemaining().toLowerCase())) {
                                            suggestionsBuilder.suggest(key);
                                        }
                                    }
                                    return suggestionsBuilder.buildFuture();
                                }))
                                .executes(this::speedjoin))
        );
        commandDispatcher.register(
                literal("speedjoin").executes(this::info)
                        .then(literal("list").executes(this::list))
                        .then(literal("set")
                                .then(argument("name", StringArgumentType.string())
                                        .then(argument("id/handle", StringArgumentType.string()).executes(this::set))))
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.string()).executes(this::remove)))
        );
    }

    private int remove(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if (!dials.containsKey(name)) {
            NotificationHelper.sendFailNotification("No such speed dial: " + name, 5);
            return 1;
        }

        dials.remove(name);
        NotificationHelper.sendAppliedNotification("Removed dial: " + name, 3);

        return 0;
    }

    private int info(CommandContext<FabricClientCommandSource> context) {
        MessageHelper.message(ColorPalette.withColor("Use /speedjoin to add sjoins", Color.PURPLE));
        MessageHelper.message(ColorPalette.withColor("Usage: /speedjoin set <name> <id/handle>", Color.LIGHT_PURPLE));
        MessageHelper.message(ColorPalette.withColor("Usage: /speedjoin list", Color.LIGHT_PURPLE));
        return 0;
    }

    private int set(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        String handle = StringArgumentType.getString(context, "id/handle");

        if (dials.containsKey(name)) {
            NotificationHelper.sendAppliedNotification("Replaced " + dials.get(name) + " with " + handle, 5);
        } else {
            NotificationHelper.sendAppliedNotification("Added " + name + " with " + handle, 3);
        }

        dials.put(name, handle);
        return 0;
    }

    private int speedjoin(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");

        if (!dials.containsKey(name)) {
            NotificationHelper.sendFailNotification("No such speed dial: " + name, 5);
            return 1;
        }

        CommandSender.queue("join " + dials.get(name));

        return 0;
    }

    private int list(CommandContext<FabricClientCommandSource> context) {
        MessageHelper.message(ColorPalette.withColor("Speed Dials: ", Color.PURPLE));
        dials.keySet().stream().sorted().forEach(key -> {
            MessageHelper.message(ColorPalette.withColor(key + " → " + dials.get(key), Color.LIGHT_PURPLE));
        });
        return 0;
    }
}
