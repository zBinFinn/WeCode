package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.ClipboardHandler;
import org.zbinfinn.wecode.Color;
import org.zbinfinn.wecode.ColorPalette;
import org.zbinfinn.wecode.clipboards.ClipBoard;
import org.zbinfinn.wecode.clipboards.ClipBoards;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ColorSpaceCommands extends CommandFeature {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        register("cb", commandDispatcher);
        register("cs", commandDispatcher);
    }

    private void register(String command, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(command).then(
                        literal("list").executes(this::listClipBoards)
                ).then(
                        literal("create")
                                .then(argument("clipboard", StringArgumentType.string()).executes(this::createClipBoard))
                ).then(
                        literal("delete")
                                .then(argument("clipboard", StringArgumentType.string()).executes(this::deleteClipBoard))
                ).then(
                        literal("add")
                                .then(argument("clipboard", StringArgumentType.string()).then(argument("name", StringArgumentType.string()).then(argument("value", StringArgumentType.greedyString()).executes(this::addValue))))
                ).then(
                        literal("remove")
                                .then(argument("clipboard", StringArgumentType.string()).then(argument("name", StringArgumentType.string()).executes(this::removeValue)))
                ).then(
                        literal("setactive")
                                .then(argument("clipboard", StringArgumentType.string()).executes(this::setActiveClipBoard))
                ).then(
                        literal("export")
                                .then(argument("clipboard", StringArgumentType.string()).executes(this::exportClipBoard))
                ).then(
                        literal("importclipboard")
                                .then(argument("new name", StringArgumentType.string()).executes(this::importClipBoard))
                ).then(
                        literal("view")
                                .then(argument("clipboard", StringArgumentType.string()).executes(this::viewClipBoard))
                )
        );
    }

    private int viewClipBoard(CommandContext<FabricClientCommandSource> context) {
        String colorSpaceName = context.getArgument("clipboard", String.class);
        ClipBoard clipBoard = ClipBoards.getBoard(colorSpaceName);
        if (clipBoard == null) {
            return 1;
        }

        MessageHelper.message(ColorPalette.withColor("Clipboard " + colorSpaceName + ":", Color.LIGHT_PURPLE));
        clipBoard.print();

        return 0;
    }

    private int importClipBoard(CommandContext<FabricClientCommandSource> context) {
        String newName = StringArgumentType.getString(context, "new name");

        String clipboard = ClipboardHandler.getClipboard();
        Optional<ClipBoard> colorSpaceOpt = ClipBoard.fromJSON(clipboard);
        if (colorSpaceOpt.isEmpty()) {
            NotificationHelper.sendFailNotification("Failed to load clipboard (Invalid Json?)", 5);
            return 0;
        }

        ClipBoard clipBoard = colorSpaceOpt.get();
        ClipBoards.getBoards().put(newName, clipBoard);
        NotificationHelper.sendAppliedNotification("Imported clipboard: " + newName, 5);
        return 0;
    }

    private int exportClipBoard(CommandContext<FabricClientCommandSource> context) {
        String colorSpaceName = StringArgumentType.getString(context, "clipboard");
        ClipBoard clipBoard = ClipBoards.getBoard(colorSpaceName);
        if (clipBoard == null) {
            return 0;
        }

        NotificationHelper.sendAppliedNotification("Copied Board '" + colorSpaceName + "' to clipboard!", 5);
        ClipboardHandler.setClipboard(clipBoard.toJSON().toString());

        return 0;
    }

    private int removeValue(CommandContext<FabricClientCommandSource> context) {
        String colorspaceName = StringArgumentType.getString(context, "clipboard");
        String name = StringArgumentType.getString(context, "name");

        ClipBoards.removeValue(colorspaceName, name);
        return 0;
    }

    private int setActiveClipBoard(CommandContext<FabricClientCommandSource> context) {
        String colorspace = StringArgumentType.getString(context, "clipboard");

        ClipBoards.setActiveBoard(colorspace);

        return 0;
    }

    private int addValue(CommandContext<FabricClientCommandSource> context) {
        String clipboardName = StringArgumentType.getString(context, "clipboard");
        String valueName = StringArgumentType.getString(context, "name");
        String value = StringArgumentType.getString(context, "value");

        ClipBoards.addValue(clipboardName, valueName, value);
        NotificationHelper.sendAppliedNotification("Added Value '" + value + "' to Clipboard '" + clipboardName + "' as '" + valueName + "'", 5);

        return 0;
    }

    private int deleteClipBoard(CommandContext<FabricClientCommandSource> context) {
        String colorspaceName = StringArgumentType.getString(context, "clipboard");
        if (colorspaceName.equals("global")) {
            NotificationHelper.sendFailNotification("Can't delete global clipboard", 5);
            return 0;
        }

        if (colorspaceName.equals(ClipBoards.getActiveBoard())) {
            NotificationHelper.sendFailNotification("Can't delete active clipboard, consider '/cb setactive global'", 5);
            return 0;
        }

        ClipBoards.deleteBoard(colorspaceName);

        return 0;
    }

    private int listClipBoards(CommandContext<FabricClientCommandSource> ctx) {
        NotificationHelper.sendAppliedNotification("Listing Clipboards", 2);

        MessageHelper.message(ColorPalette.withColor("Listing Clipboards: ", Color.PURPLE));

        for (String csName : ClipBoards.getBoards().keySet()) {
            ClipBoard cs = ClipBoards.getBoards().get(csName);
            MessageHelper.message(ColorPalette.withColor("Clipboard " + csName + ":", Color.LIGHT_PURPLE));
            cs.print();
        }
        MessageHelper.message("");
        if (ClipBoards.getActiveBoard().equals("")) {
            MessageHelper.message(ColorPalette.withColor("No active clipboard", Color.LIGHT_PURPLE));
        } else {
            MessageHelper.message(ColorPalette.withColor("☞ Active Clipboard: '" + ClipBoards.getActiveBoard() + "'", Color.LIGHT_PURPLE));
        }

        return 0;
    }

    private int createClipBoard(CommandContext<FabricClientCommandSource> ctx) {
        String clipboardName = StringArgumentType.getString(ctx, "clipboard");
        ClipBoards.createBoard(clipboardName);
        return 0;
    }
}
