package org.zbinfinn.wecode.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {
    public static @Nullable NbtCompound getItemTags(ItemStack item) {
        NbtComponent data = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = data.copyNbt();

        return nbt.getCompound("PublicBukkitValues").orElse(null);
    }
}
