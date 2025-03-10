package org.zbinfinn.wecode;

import net.minecraft.network.packet.Packet;

public class PacketSender {
    public static void sendPacket(Packet<?> packet) {
        WeCode.MC.getNetworkHandler().sendPacket(packet);
    }
    public static void sendChatMessage(String message) {
        WeCode.MC.getNetworkHandler().sendChatMessage(message);
    }
}
