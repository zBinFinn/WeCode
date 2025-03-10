package org.zbinfinn.wecode;

import org.zbinfinn.wecode.helpers.MessageHelper;

import java.util.LinkedList;
import java.util.Queue;

public class CommandSender {
    private static int commandCooldown = 0;
    private static final Queue<String> commands = new LinkedList<>();

    /*
    DF has an internal cooldown that goes up by 20 everytime a command is sent and
    goes down by 1 per tick, if this number exceeds 200 commands get rate limited,
    accounting for lag this command sender only sends commands when that value is below 140
     */

    public static void tick() {
        if (commandCooldown > 0) {
            commandCooldown--;
        }
        if (commandCooldown < 140) {
            if (commands.peek() != null) {
                WeCode.MC.getNetworkHandler().sendCommand(commands.poll());
                commandCooldown += 20;
            }
        }
    }

    public static void queue(String command) {
        if (commandCooldown > 200) {
            MessageHelper.debug("Command cooldown exceeded, command: `" + command + "` wasn't sent");
            return;
        }
        commands.add(command);
    }

    public static void queueImportant(String command) {
        commands.add(command);
    }

    public static void queueDelay(String command, int ms) {
        new Thread(() -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignored) {}
            queue(command);
        }).start();
    }
}
