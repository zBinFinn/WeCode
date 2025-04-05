package org.zbinfinn.wecode.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.dfonline.flint.feature.trait.CommandFeature;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.ArrayList;
import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SaveLoadInvCommand implements CommandFeature {
    private ArrayList<ItemStack> items;

    @Override
    public String commandName() {
        return "inventory";
    }

    @Override
    public Set<String> aliases() {
        return Set.of(
                "inv"
        );
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(LiteralArgumentBuilder<FabricClientCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        return builder
                .then(literal("save").executes(this::save))
                .then(literal("load").executes(this::load));
    }

    private int load(CommandContext<FabricClientCommandSource> context) {
        if (!WeCode.MC.player.isInCreativeMode()) {
            NotificationHelper.sendFailNotification("You have to be in creative mode to load inventories", 3);
            return 0;
        }
        for (int i = 0; i < WeCode.MC.player.getInventory().size(); i++) {
            WeCode.MC.player.getInventory().setStack(i, items.get(i).copy());
        }
        NotificationHelper.sendAppliedNotification("Inventory Loaded", 3);
        return 0;
    }

    private int save(CommandContext<FabricClientCommandSource> context) {
        if (items == null) {
            items = new ArrayList<>(WeCode.MC.player.getInventory().size());
            for (int i = 0; i < WeCode.MC.player.getInventory().size(); i++) {
                items.add(ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < WeCode.MC.player.getInventory().size(); i++) {
            items.set(i, WeCode.MC.player.getInventory().getStack(i).copy());
        }
        NotificationHelper.sendAppliedNotification("Inventory Saved", 3);
        return 0;
    }
}
