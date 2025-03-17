package org.zbinfinn.wecode.features.commands.targetedjoincommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.features.commands.CommandFeature;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DevIDCommand extends CommandFeature {
    public static boolean devving = false;
    private long initialTime;

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(
                literal("dev")
                        .then(argument("plot id", IntegerArgumentType.integer(1)).executes(this::runID))
                        .then(argument("plot handle", StringArgumentType.string()).executes(this::runHandle))
        );
    }

    private int runHandle(CommandContext<FabricClientCommandSource> context) {
        String handle = StringArgumentType.getString(context, "plot handle");
        run(handle);
        return 0;
    }

    private void run(String plot) {
        if (devving || BuildIDCommand.building) {
            return;
        }

        devving = true;
        initialTime = System.currentTimeMillis();
        CommandSender.queue("join " + plot);
    }

    private int runID(CommandContext<FabricClientCommandSource> context) {
        int plotId = IntegerArgumentType.getInteger(context, "plot id");
        run("" + plotId);
        return 0;
    }


    @Override
    public void handlePacket(Packet<?> packetU, CallbackInfo ci) {
        if (!(packetU instanceof GameMessageS2CPacket packet)) {
            return;
        }
        if (!devving) {
            return;
        }

        if (System.currentTimeMillis() - initialTime > 10000) {
            NotificationHelper.sendFailNotification("/dev <id> request timed out", 3);
            devving = false;
            return;
        }

        String message = packet.content().getString();
        if (!message.startsWith("» Joined game: ")) {
            return;
        }

        CommandSender.queue("dev");
        devving = false;
        ci.cancel();
    }
}
