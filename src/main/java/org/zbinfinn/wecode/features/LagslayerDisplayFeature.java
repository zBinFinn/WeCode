package org.zbinfinn.wecode.features;

import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.RenderedFeature;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.RegExp;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.util.FormattingUtil;

public class LagslayerDisplayFeature implements PacketListeningFeature, RenderedFeature {
    private double usage = 0;
    private long lastUpdate = 0;

    @Override
    public boolean isEnabled() {
        return Config.getConfig().CPUDisplay;
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        if (!(packet instanceof OverlayMessageS2CPacket(Text text))) {
            return EventResult.PASS;
        }

        String message = text.getString();

        @RegExp String regex = "CPU Usage: \\[▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮] \\(\\d\\d\\.\\d\\d%\\)";
        if (!message.matches(regex)) {
            return EventResult.PASS;
        }

        String stripped = message.substring("CPU Usage: [▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮] (".length(), message.length() - 2);
        usage = Double.parseDouble(stripped);
        lastUpdate = System.currentTimeMillis();
        return EventResult.CANCEL;
    }

    @Override
    public void render(DrawContext draw, RenderTickCounter renderTickCounter) {
        if (System.currentTimeMillis() - lastUpdate > 3000) {
            return;
        }

        draw.drawTextWithShadow(WeCode.MC.textRenderer,
                Text.literal("CPU Usage: ").styled(style -> style.withColor(0xFFAA88)).append(
                        Text.literal(FormattingUtil.numberAsString(usage, 2) + "%").styled(style -> style.withColor(0xFFFFFF)))


                , 10, 10, 0xFF_FFFFFF);

    }
}
