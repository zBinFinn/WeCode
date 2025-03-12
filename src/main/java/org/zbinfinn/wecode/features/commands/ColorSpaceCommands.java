package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import org.zbinfinn.wecode.ClipboardHandler;
import org.zbinfinn.wecode.ColorPalette;
import org.zbinfinn.wecode.colorspaces.Color;
import org.zbinfinn.wecode.colorspaces.ColorSpace;
import org.zbinfinn.wecode.colorspaces.ColorSpaces;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ColorSpaceCommands extends Feature implements ClientCommandRegistrationCallback {
    @Override
    public void activate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("cs").then(
                        literal("list").executes(this::listColorSpaces)
                ).then(
                        literal("create").then(argument("colorspace", StringArgumentType.string()).executes(this::createColorSpace))
                ).then(
                        literal("delete").then(argument("colorspace", StringArgumentType.string()).executes(this::deleteColorSpace))
                ).then(
                        literal("add").then(argument("colorspace", StringArgumentType.string()).then(argument("colorname", StringArgumentType.string()).then(argument("color", StringArgumentType.greedyString()).executes(this::addColor))))
                ).then(
                        literal("remove").then(argument("colorspace", StringArgumentType.string()).then(argument("colorname", StringArgumentType.string()).executes(this::removeColor)))
                ).then(
                        literal("setactive").then(argument("colorspace", StringArgumentType.string()).executes(this::setActiveColorSpace))
                ).then(
                        literal("export").then(argument("colorspace", StringArgumentType.string()).executes(this::exportColorSpace))
                ).then(
                        literal("importclipboard").then(argument("new name", StringArgumentType.string()).executes(this::importColorSpace))
                ).then(
                        literal("view").then(argument("colorspace", StringArgumentType.string()).executes(this::viewColorSpace))
                )
        );
    }

    private int viewColorSpace(CommandContext<FabricClientCommandSource> context) {
        String colorSpaceName = context.getArgument("colorspace", String.class);
        ColorSpace colorSpace = ColorSpaces.getSpace(colorSpaceName);
        if (colorSpace == null) {
            return 1;
        }

        MessageHelper.message(ColorPalette.withColor("Colorspace " + colorSpaceName + ":", ColorPalette.Colors.LIGHT_PURPLE));
        colorSpace.print();

        return 0;
    }

    private int importColorSpace(CommandContext<FabricClientCommandSource> context) {
        String newName = StringArgumentType.getString(context, "new name");

        String clipboard = ClipboardHandler.getClipboard();
        Optional<ColorSpace> colorSpaceOpt = ColorSpace.fromJSON(clipboard);
        if (colorSpaceOpt.isEmpty()) {
            NotificationHelper.sendFailNotification("Failed to load colorspace (Invalid Json?)", 5);
            return 0;
        }

        ColorSpace colorSpace = colorSpaceOpt.get();
        ColorSpaces.getSpaces().put(newName, colorSpace);
        NotificationHelper.sendAppliedNotification("Imported colorspace: " + newName, 5);
        return 0;
    }

    private int exportColorSpace(CommandContext<FabricClientCommandSource> context) {
        String colorSpaceName = StringArgumentType.getString(context, "colorspace");
        ColorSpace colorSpace = ColorSpaces.getSpace(colorSpaceName);
        if (colorSpace == null) {
            return 0;
        }

        NotificationHelper.sendAppliedNotification("Copied Colorspace '" + colorSpaceName + "' to clipboard!", 5);
        ClipboardHandler.setClipboard(colorSpace.toJSON().toString());

        return 0;
    }

    private int removeColor(CommandContext<FabricClientCommandSource> context) {
        String colorspaceName = StringArgumentType.getString(context, "colorspace");
        String colorName = StringArgumentType.getString(context, "colorname");

        ColorSpaces.removeColor(colorspaceName, colorName);
        return 0;
    }

    private int setActiveColorSpace(CommandContext<FabricClientCommandSource> context) {
        String colorspace = StringArgumentType.getString(context, "colorspace");

        ColorSpaces.setActiveSpace(colorspace);

        return 0;
    }

    private int addColor(CommandContext<FabricClientCommandSource> context) {
        String colorspaceName = StringArgumentType.getString(context, "colorspace");
        String colorName = StringArgumentType.getString(context, "colorname");
        String color = StringArgumentType.getString(context, "color");

        if (!color.matches("#[0-9a-fA-F]{6}")) {
            NotificationHelper.sendFailNotification(color + " is not a valid colorcode [#xxxxxx]", 6);
            return 0;
        }

        ColorSpaces.addColor(colorspaceName, colorName, color);

        return 0;
    }

    private int deleteColorSpace(CommandContext<FabricClientCommandSource> context) {
        String colorspaceName = StringArgumentType.getString(context, "colorspace");
        if (colorspaceName.equals("global")) {
            NotificationHelper.sendFailNotification("Can't delete global colorspace", 5);
            return 0;
        }

        if (colorspaceName.equals(ColorSpaces.getActiveSpace())) {
            NotificationHelper.sendFailNotification("Can't delete active colorspace, consider '/cs setactive global'", 5);
            return 0;
        }

        ColorSpaces.deleteSpace(colorspaceName);

        return 0;
    }

    private int listColorSpaces(CommandContext<FabricClientCommandSource> ctx) {
        NotificationHelper.sendAppliedNotification("Listing Colorspaces", 2);

        MessageHelper.message(ColorPalette.withColor("Listing Colorspaces: ", ColorPalette.Colors.PURPLE));

        for (String csName : ColorSpaces.getSpaces().keySet()) {
            ColorSpace cs = ColorSpaces.getSpaces().get(csName);
            MessageHelper.message(ColorPalette.withColor("Colorspace " + csName + ":", ColorPalette.Colors.LIGHT_PURPLE));
            cs.print();
        }
        MessageHelper.message("");
        if (ColorSpaces.getActiveSpace().equals("")) {
            MessageHelper.message(ColorPalette.withColor("No active colorspace", ColorPalette.Colors.LIGHT_PURPLE));
        } else {
            MessageHelper.message(ColorPalette.withColor("☞ Active Colorspace: '" + ColorSpaces.getActiveSpace() + "'", ColorPalette.Colors.LIGHT_PURPLE));
        }

        return 0;
    }

    private int createColorSpace(CommandContext<FabricClientCommandSource> ctx) {
        String colorspaceName = StringArgumentType.getString(ctx, "colorspace");
        ColorSpaces.createSpace(colorspaceName);
        return 0;
    }
}
