package org.zbinfinn.wecode.functioncategories;

import net.minecraft.item.ItemStack;

public class Function {
    private ItemStack icon;
    private String name;

    public Function(ItemStack icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Function [name=" + name + ", icon=" + icon + "]";
    }
}
