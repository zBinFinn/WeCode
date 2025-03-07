package org.zbinfinn.wecode;

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
            }
        }
    }

    public static void queue(String command) {
        commands.add(command);
        commandCooldown += 20;
    }
}
