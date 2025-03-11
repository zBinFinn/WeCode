package org.zbinfinn.wecode.features;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

public abstract class Feature {
    private ChestFeature chestFeature;

    @Nullable
    protected ChestFeature makeChestFeature(HandledScreen<?> screen) {
        return null;
    }
    public final void openChest(HandledScreen<?> screen) {
        chestFeature = makeChestFeature(screen);
    }
    public final void closeChest() {
        chestFeature = null;
    }
    public final Optional<ChestFeature> getChestFeature() {
        return Optional.ofNullable(chestFeature);
    }

    public void activate() {}
    public void tick() {}
    public void tooltip(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {}
    public boolean isEnabled() { return true; }
    public void hudRender(DrawContext draw, RenderTickCounter tickCounter) {}
    public void handlePacket(Packet<?> packet, CallbackInfo ci) {}
    public void sentPacket(Packet<?> packet, CallbackInfo ci) {}
    public void worldRenderLast(WorldRenderContext event) {}
    public void clientStop(MinecraftClient client) {}
    public String handleChatMessage(String message) {
        return message;
    }
}
