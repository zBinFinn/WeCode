package org.zbinfinn.wecode.template_editor.refactor.handler;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.templates.Template;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.util.math.Direction;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.PacketHelper;
import org.zbinfinn.wecode.plotdata.LineStarter;

public class TemplatePlacerHandler {
    private enum State {
        STARTED_TELEPORT,
        FINISHED_TELEPORT,
        STARTED_BREAKING,
        PLACING,
        NONE
    }

    private State state = State.NONE;
    private LineStarter lineStarter;
    private Template template;

    public void place(Template template, LineStarter lineStarter) {
        if (state != State.NONE) {
            return;
        }
        this.lineStarter = lineStarter;
        this.template = template;
        state = State.STARTED_TELEPORT;
        CommandSender.queue("ctp " + lineStarter.getType() + " " + lineStarter.getName());
    }

    public void packet(Packet<?> packet) {
        switch (state) {
            case STARTED_TELEPORT -> {
                if (!(packet instanceof BundleS2CPacket bundle)) {
                    return;
                }

                PlayerPositionLookS2CPacket lookPacket = null;
                for (var pac : bundle.getPackets()) {
                    if (pac instanceof PlayerPositionLookS2CPacket look) {
                        lookPacket = look;
                    }
                }
                if (lookPacket == null) {
                    return;
                }

                state = State.FINISHED_TELEPORT;
                return;
            }
            case STARTED_BREAKING -> {
                if (!(packet instanceof ScreenHandlerSlotUpdateS2CPacket realPacket)) {
                    return;
                }

                state = State.PLACING;
                return;
            }
        }
    }

    public void tick() {
        switch (state) {
            case FINISHED_TELEPORT -> {
                var player = Flint.getUser().getPlayer();

                PacketHelper.setFlying(true);
                PacketHelper.sendSneaking(true);
                state = State.STARTED_BREAKING;
                PacketHelper.breakBlock(player.getBlockPos().add(0, -2, 0));
                PacketHelper.sendSneaking(false);
                return;
            }
            case PLACING -> {
                var player = Flint.getUser().getPlayer();

                PacketHelper.sendHandItem(template.toItem());
                state = State.NONE;
                PacketHelper.rightClickBlock(player.getBlockPos().add(0, -3, 0), Direction.UP);
                PacketHelper.sendHandItem(ItemStack.EMPTY);
                return;
            }
        }
    }
}
