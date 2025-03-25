package org.zbinfinn.wecode.features;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.core.FeatureTrait;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.helpers.MessageHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public interface DebugFeature extends FeatureTrait {
    @Override
    default boolean isEnabled() {
        return Config.getConfig().Debug;
    }
}
