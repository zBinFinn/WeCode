package org.zbinfinn.wecode;

import dev.dfonline.flint.Flint;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PacketHelper {
    public static void sendSneaking(boolean sneaking) {
        send(new ClientCommandC2SPacket(Flint.getUser().getPlayer(),
                                        sneaking ?
                                           ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY :
                                           ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY)
        );
    }

    public static void rightClickBlock(BlockPos pos) {
        WeCode.MC.interactionManager.interactBlock(
            Flint.getUser().getPlayer(),
            Hand.MAIN_HAND,
            new BlockHitResult(
                pos.toCenterPos(),
                Direction.UP,
                pos,
                false
            )
        );
    }

    public static void rightClickBlock(BlockPos pos, Direction direction) {
        WeCode.MC.interactionManager.interactBlock(
            Flint.getUser().getPlayer(),
            Hand.MAIN_HAND,
            new BlockHitResult(
                pos.toCenterPos(),
                direction,
                pos,
                false
            )
        );
    }

    public static void setFlying(boolean flying) {
        var player = Flint.getUser().getPlayer();
        player.getAbilities().flying = flying;
        UpdatePlayerAbilitiesC2SPacket packet = new UpdatePlayerAbilitiesC2SPacket(
            player.getAbilities()
        );
    }

    public static void breakBlock(BlockPos pos) {
        PlayerActionC2SPacket packet = new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
            pos,
            Direction.UP
        );
        send(packet);
    }

    public static void sendHandItem(ItemStack item) {
        send(new CreativeInventoryActionC2SPacket(36 + WeCode.MC.player.getInventory().selectedSlot, item));
        WeCode.MC.player.getInventory().setStack(WeCode.MC.player.getInventory().selectedSlot, item);
    }








    private static void send(Packet packet) {
        WeCode.MC.getNetworkHandler().sendPacket(packet);
    }
}
