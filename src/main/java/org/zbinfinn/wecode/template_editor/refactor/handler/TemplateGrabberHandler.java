package org.zbinfinn.wecode.template_editor.refactor.handler;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.templates.Template;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.PacketHelper;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;

public class TemplateGrabberHandler {
    private enum CachingState {
        NONE,
        TELEPORTING,
        RECEIVED_TELEPORT,
        GRABBING,
    }

    public interface Callback {
        void callback(Template template, LineStarter lineStarter);
    }

    private CachingState cachingState = CachingState.NONE;
    private Callback callback;
    private LineStarter lineStarter;

    public void grab(LineStarter lineStarter, Callback callback) {
        if (cachingState != CachingState.NONE) {
            WeCode.LOGGER.error("Tried grabbing template before grabbing was done :(");
            return;
        }
        this.callback = callback;
        this.lineStarter = lineStarter;
        CommandSender.queue("ctp " + lineStarter.getType() + " " + lineStarter.getName());
        cachingState = CachingState.TELEPORTING;
    }

    public void receivePacket(Packet<?> packet) {
        switch (cachingState) {
            case TELEPORTING -> teleportingPacket(packet);
            case GRABBING -> grabbingPacket(packet);
        };
    }

    private void teleportingPacket(Packet<?> packet) {
        if (!(packet instanceof BundleS2CPacket bundle)) {
            return;
        }

        PlayerPositionLookS2CPacket lookPacket = null;
        for (var pac : bundle.getPackets()) {
            if (pac instanceof PlayerPositionLookS2CPacket) {
                lookPacket = (PlayerPositionLookS2CPacket) pac;
            }
        }
        if (lookPacket == null) {
            return;
        }

        Flint.getUser().getPlayer().sendMessage(Text.of("Received Teleport"), false);
        cachingState = CachingState.RECEIVED_TELEPORT;
    }

    private void grabbingPacket(Packet<?> packet) {
        if (!(packet instanceof ScreenHandlerSlotUpdateS2CPacket realPacket)) {
            return;
        }

        cachingState = CachingState.NONE;
        Flint.getUser().getPlayer().sendMessage(realPacket.getStack().getName(), false);
        Template template = Template.fromItem(realPacket.getStack());
        callback.callback(template, lineStarter);
        PacketHelper.sendHandItem(ItemStack.EMPTY);
    }

    public void tick() {
        if (cachingState == CachingState.RECEIVED_TELEPORT) {
            cachingState = CachingState.GRABBING;
            var player = Flint.getUser().getPlayer();
            ItemStack handItem = player.getMainHandStack();

            PacketHelper.sendSneaking(true);
            PacketHelper.rightClickBlock(player.getBlockPos().add(0, -2, 0));
            PacketHelper.sendSneaking(false);
        }
    }

}
