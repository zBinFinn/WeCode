package org.zbinfinn.wecode.features.keybinds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.zbinfinn.wecode.GUIKeyBinding;
import org.zbinfinn.wecode.features.Feature;
import org.zbinfinn.wecode.util.ItemUtil;

import java.util.List;

public class ShowItemTagsKeybind extends Feature {
    private final GUIKeyBinding keybind = new GUIKeyBinding(
            "key.wecode.showtags",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_LEFT_ALT,
            "key.wecode.category"
    );

    @Override
    public void activate() {
        KeyBindingHelper.registerKeyBinding(keybind);
    }

    @Override
    public void tooltip(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list, boolean isCustom) {
        if (isCustom) {
            return;
        }
        if (!keybind.isPressed()) {
            return;
        }

        list.add(Text.empty());
        list.add(Text.literal("Tags:").styled(style -> style.withColor(Formatting.GRAY)));
        NbtCompound nbt = ItemUtil.getItemTags(item);
        for (String key : nbt.getKeys()) {
            String formattedKey = key.substring(10);
            Text name = Text.literal(formattedKey).styled(s -> s.withColor(TextColor.fromRgb(0xff88cc)))
                            .append(Text.literal(" = ").styled(s -> s.withColor(Formatting.DARK_GRAY)));
            Text value;
            if (!nbt.getString(key).isEmpty()) {
                value = Text.literal(nbt.getString(key)).styled(s -> s.withColor(TextColor.fromRgb(0x88ffff)));
            } else {
                value = Text.literal(String.valueOf(nbt.getDouble(key))).styled(s -> s.withColor(TextColor.fromRgb(0xff8888)));
            }

            list.add(name.copy().append(value));

        }

    }
}
