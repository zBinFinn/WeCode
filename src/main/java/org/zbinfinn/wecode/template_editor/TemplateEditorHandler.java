package org.zbinfinn.wecode.template_editor;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.PacketListeningFeature;
import dev.dfonline.flint.feature.trait.TickedFeature;
import dev.dfonline.flint.templates.Template;
import dev.dfonline.flint.util.message.impl.prefix.ErrorMessage;
import dev.dfonline.flint.util.result.EventResult;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.CommandSender;
import org.zbinfinn.wecode.PacketHelper;
import org.zbinfinn.wecode.ScreenHandler;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.plotdata.LineStarter;
import org.zbinfinn.wecode.plotdata.PlotDataManager;


public class TemplateEditorHandler implements TickedFeature, PacketListeningFeature {
    public TemplateEditorScreen screen;

    public void open() {
        if (screen == null) {
            screen = new TemplateEditorScreen();
        }
        PlotDataManager.cacheLineStarters();
        screen.setCaching(true);
        screen.init(WeCode.MC, WeCode.MC.getWindow().getScaledWidth(), WeCode.MC.getWindow().getScaledHeight());
        ScreenHandler.scheduleOpenScreen(screen);
    }

    public void addTemplateItem(ItemStack mainHandStack) {
        try {
            Template template = Template.fromItem(mainHandStack);
            //Template template = Template.fromJson("{\"name\":\"Exported\",\"author\":\"WeCode TEditor\",\"code\":\"H4sIAAAAAAAA/62QsQrCMBCGX0VudnDOKOimS92klJieNTRNanKCteTdvbQdROmgOOXuv8vHx/VwMk7VAcSxB12CGHtYTq+A1sgOfSEVaWc5nwoBGdpyhyHIClPsK4Ywg7CZaFwNSaLSnXiplCRTZGWDHB4w0CIjr20FMS4hGEcgVjHnZsZGn4tR6NVkz7jN9SZN+FHksdZ2q62dtfBS1Th81B4Vj8G1mK5BXZsA1vkGZqX/c0LlmvZTPaPO6HD5wlwZF/BdPY9PrNT83AoCAAA=\"}");
            addTemplate(template);
        } catch (Exception err) {
            Flint.getUser().sendMessage(
                new ErrorMessage("Something went wrong while adding a template item :(")
            );
            err.printStackTrace();
        }


    }

    public void addTemplate(Template template) {
        screen.addTemplate(template);
    }

    public void reset() {
        screen = null;
    }

    private CachingState cachingState = CachingState.NONE;
    private String lookingFor = "";

    public void addTemplateItemFromLinestarter(LineStarter lineStarter) {
        cachingState = CachingState.TELEPORTING;
        lookingFor = lineStarter.getName();
        System.out.println("Started Caching");
        CommandSender.queue("ctp " + lineStarter.getType() + " " + lineStarter.getName());
    }

    private enum CachingState {
        NONE,
        TELEPORTING,
        RECEIVED_TELEPORT,
        GRABBING,
    }

    @Override
    public EventResult onReceivePacket(Packet<?> packet) {
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
        try {
            Template newTemplate = Template.fromItem(realPacket.getStack());
            newTemplate.setName(lookingFor);
            addTemplate(newTemplate);
        } catch (Exception err) {
            Flint.getUser().getPlayer().sendMessage(
                Text.literal("Something went horribly wrong when grabbing that template :("), false
            );
            err.printStackTrace();
        }
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
