package org.zbinfinn.wecode.features;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public abstract class Feature {
    public void activate() {}
    public void tick() {}
    public void tooltip(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {}
    public boolean isEnabled() { return true; }
    public void hudRender(DrawContext draw, RenderTickCounter tickCounter) {}
    public void handlePacket(Packet<?> packet, CallbackInfo ci) {}
    public void sentPacket(Packet<?> packet, CallbackInfo ci) {}
}
