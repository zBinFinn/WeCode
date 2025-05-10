package org.zbinfinn.wecode.features.keybinds;

import dev.dfonline.flint.Flint;
import dev.dfonline.flint.feature.trait.TooltipRenderFeature;
import dev.dfonline.flint.hypercube.Mode;
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
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.config.Config;
import org.zbinfinn.wecode.util.ItemUtil;

import java.util.List;
import java.util.Set;

public class ShowItemTagsKeybind implements TooltipRenderFeature {
    private final GUIKeyBinding keybind = new GUIKeyBinding(
            "key.wecode.showtags",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_LEFT_ALT,
            "key.wecode.category"
    );

    private static final Set<String> IGNORED_TAGS = Set.of(
            "varitem",
            "item_instance"
    );

    public ShowItemTagsKeybind() {
        KeyBindingHelper.registerKeyBinding(keybind);
    }

    @Override
    public void tooltipRender(ItemStack item, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {
        if (WeCode.isDrawingCustomTooltip()) {
            return;
        }
        ShowState state = shouldShow();
        if (state == ShowState.NONE) {
            return;
        }

        NbtCompound nbt = ItemUtil.getItemTags(item);
        if (nbt.getKeys().isEmpty()) {
            return;
        }
        list.add(Text.empty());
        list.add(Text.literal("Tags:").styled(style -> style.withColor(Formatting.GRAY)));
        for (String key : nbt.getKeys()) {
            String formattedKey = key.substring(10);
            if (state == ShowState.NORMAL) {
                if (IGNORED_TAGS.contains(formattedKey)) {
                    continue;
                }
            }
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

    enum ShowState {
        FULL,
        NORMAL,
        NONE,
    }

    private ShowState shouldShow() {
        if (keybind.isPressed()) {
            return ShowState.FULL;
        }
        if (Flint.getUser().getMode() == Mode.DEV && Config.getConfig().ShowTagsInDev) {
            return ShowState.NORMAL;
        }
        return ShowState.NONE;
    }
}
