package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.templates.CodeBlocks;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.templates.codeblock.Function;
import dev.dfonline.flint.templates.codeblock.Process;
import dev.dfonline.flint.util.message.impl.prefix.ErrorMessage;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.PacketHelper;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.PlotDataManager;

public class TemplateEditorHandler implements TickedFeature, PacketListeningFeature {
    public TemplateEditorScreen screen = null;

    @Override
    public EventResult onSendPacket(Packet<?> packet) {
        if (packet instanceof ClientTickEndC2SPacket) return EventResult.PASS;
        if (packet instanceof PlayerMoveC2SPacket) return EventResult.PASS;
        if (packet instanceof KeepAliveS2CPacket) return EventResult.PASS;
        if (packet instanceof PlayerInteractBlockC2SPacket interact) {
            System.out.println(interact.getBlockHitResult().getSide());
        }
        if (packet instanceof UpdatePlayerAbilitiesC2SPacket abil) {
            System.out.println(abil.isFlying());
        }
        System.out.println("[S] " + packet.getClass().getName());
        return EventResult.PASS;
    }

    public void open() {
        if (screen == null) {
            screen = new TemplateEditorScreen();
        }
        PlotDataManager.cacheLineStarters();
        screen.setCaching(true);
        screen.init(WeCode.MC, WeCode.MC.getWindow().getScaledWidth(), WeCode.MC.getWindow().getScaledHeight());
        ScreenHandler.scheduleOpenScreen(screen);
    }

    public void addTemplateItem(ItemStack item, LineStarter lineStarter) {
        try {
            Template template = Template.fromItem(item);
            addTemplate(template, lineStarter);
        } catch (Exception err) {
            Flint.getUser().sendMessage(
                new ErrorMessage("Something went HORRENDOUSLY wrong when trying to turn an item with a linestarter into a template: " + err.getMessage())
            );
            err.printStackTrace();
        }
    }

    public void addTemplateItem(ItemStack mainHandStack) {
        try {
            Template template = Template.fromItem(mainHandStack);

            LineStarter lineStarter = getLineStarterFromTemplate(template);

            if (lineStarter == null) {
                throw new RuntimeException("Template doesnt start with a linestarter");
            }

            addTemplate(template, lineStarter);
        } catch (Exception err) {
            Flint.getUser().sendMessage(
                new ErrorMessage("Something went wrong while adding a template item :( " + err.getMessage())
            );
            err.printStackTrace();
        }


    }

    private static LineStarter getLineStarterFromTemplate(Template template) {
        CodeBlocks codeBlocks = template.getBlocks();
        LineStarter lineStarter = null;
        if (codeBlocks.getBlocks().getFirst() instanceof Function fun) {
            lineStarter = new org.zbinfinn.wecode.plotdata.linestarters.Function(fun.getFunctionName());
        } else if (codeBlocks.getBlocks().getFirst() instanceof Process proc) {
            lineStarter = new org.zbinfinn.wecode.plotdata.linestarters.Process(proc.getProcessName());
        }
        return lineStarter;
    }

    public void addTemplate(Template template, LineStarter lineStarter) {
        screen.addTemplate(template, lineStarter);
    }

    public void reset() {
        screen = null;
    }

    private CachingState cachingState = CachingState.NONE;
    private LineStarter lookingFor;

    public void addTemplateItemFromLinestarter(LineStarter lineStarter) {
        cachingState = CachingState.TELEPORTING;
        lookingFor = lineStarter;
        System.out.println("Started Caching");
        CommandSender.queue("ctp " + lineStarter.getType() + " " + lineStarter.getName());
    }

    private static class SavingTemplateData {
        public LineStarter toBeReplaced;
        public Template with;
        public SavingTemplateState state;

        public SavingTemplateData(LineStarter toBeReplaced, Template with, SavingTemplateState state) {
            this.toBeReplaced = toBeReplaced;
            this.with = with;
            this.state = state;
        }
        public SavingTemplateData() {
            this.state = SavingTemplateState.NONE;
        }
    }
    private enum SavingTemplateState {
        TELEPORTING,
        TELEPORTED,
        BREAKING,
        PLACING,
        NONE
    }

    private SavingTemplateData savingData = new SavingTemplateData();
    public void saveTemplate(TemplateEditor templateEditor) {
        savingData = new SavingTemplateData(templateEditor.getLineStarter(), templateEditor.getTemplate(), SavingTemplateState.TELEPORTING);
        CommandSender.queue("ctp " + savingData.toBeReplaced.getType() + " " + savingData.toBeReplaced.getName());
    }

    private EventResult savingTemplateReceivePacket(Packet<?> packet) {
        if (savingData.state == SavingTemplateState.TELEPORTING) {
            if (!(packet instanceof BundleS2CPacket bundle)) {
                return EventResult.PASS;
            }

            PlayerPositionLookS2CPacket lookPacket = null;
            for (var pac : bundle.getPackets()) {
                if (pac instanceof PlayerPositionLookS2CPacket) {
                    lookPacket = (PlayerPositionLookS2CPacket) pac;
                }
            }
            if (lookPacket == null) return EventResult.PASS;

            Flint.getUser().getPlayer().sendMessage(Text.of("Received Teleport"), false);
            savingData.state = SavingTemplateState.TELEPORTED;

            System.out.println(packet.getClass().getName());
            return EventResult.PASS;
        }

        if (savingData.state == SavingTemplateState.BREAKING) {
            if (!(packet instanceof ScreenHandlerSlotUpdateS2CPacket realPacket)) {
                return EventResult.PASS;
            }

            savingData.state = SavingTemplateState.PLACING;
            return EventResult.PASS;
        }

        return EventResult.PASS;
    }

    private enum CachingState {
        NONE,
        TELEPORTING,
        RECEIVED_TELEPORT,
        GRABBING,
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
        if (savingData.state != SavingTemplateState.NONE) {
            return savingTemplateReceivePacket(packet);
        }
        return switch (cachingState) {
            case TELEPORTING -> teleportingPacket(packet);
            case GRABBING -> grabbingPacket(packet);
            default -> EventResult.PASS;
        };
    }

    private EventResult grabbingPacket(Packet<?> packet) {
        if (!(packet instanceof ScreenHandlerSlotUpdateS2CPacket realPacket)) {
            return EventResult.PASS;
        }

        cachingState = CachingState.NONE;
        Flint.getUser().getPlayer().sendMessage(realPacket.getStack().getName(), false);
        addTemplateItem(realPacket.getStack(), lookingFor);
        //PacketHelper.sendHandItem(ItemStack.EMPTY);
        return EventResult.PASS;
    }

    private EventResult teleportingPacket(Packet<?> packet) {
        if (!(packet instanceof BundleS2CPacket bundle)) {
            return EventResult.PASS;
        }

        PlayerPositionLookS2CPacket lookPacket = null;
        for (var pac : bundle.getPackets()) {
            if (pac instanceof PlayerPositionLookS2CPacket) {
                lookPacket = (PlayerPositionLookS2CPacket) pac;
            }
        }
        if (lookPacket == null) return EventResult.PASS;

        Flint.getUser().getPlayer().sendMessage(Text.of("Received Teleport"), false);
        cachingState = CachingState.RECEIVED_TELEPORT;

        System.out.println(packet.getClass().getName());
        return EventResult.PASS;
    }

    @Override
    public void tick() {
        if (savingData.state == SavingTemplateState.TELEPORTED) {
            savingData.state = SavingTemplateState.BREAKING;
            var player = Flint.getUser().getPlayer();

            PacketHelper.setFlying(true);
            PacketHelper.sendSneaking(true);
            PacketHelper.breakBlock(player.getBlockPos().add(0, -2, 0));
            PacketHelper.sendSneaking(false);
            return;
        }

        if (savingData.state == SavingTemplateState.PLACING) {
            savingData.state = SavingTemplateState.NONE;
            var player = Flint.getUser().getPlayer();

            PacketHelper.sendHandItem(savingData.with.toItem());
            PacketHelper.rightClickBlock(player.getBlockPos().add(0, -3, 0), Direction.UP);

            return;
        }

        if (cachingState == CachingState.RECEIVED_TELEPORT) {
            cachingState = CachingState.GRABBING;
            var player = Flint.getUser().getPlayer();
            ItemStack handItem = player.getMainHandStack();

            PacketHelper.sendSneaking(true);
            PacketHelper.rightClickBlock(player.getBlockPos().add(0, -2, 0));
            PacketHelper.sendSneaking(false);
            return;
        }
    }
}
