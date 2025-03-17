package org.zbinfinn.wecode.features.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.zbinfinn.wecode.features.Feature;

public abstract class CommandFeature extends Feature implements ClientCommandRegistrationCallback {
    public void commandActivate() {
        ClientCommandRegistrationCallback.EVENT.register(this);
    }
}
