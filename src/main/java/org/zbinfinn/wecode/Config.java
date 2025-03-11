package org.zbinfinn.wecode;

import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.impl.controller.IntegerFieldControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.zbinfinn.wecode.util.FileUtil;

public class Config {
    public static Config instance;

    public int NormalFlightSpeed = 100;
    public int FastFlightSpeed = 300;
    public boolean TemplatePeeker = true;

    public boolean DFToNotifSuccess = true;
    public boolean DFToNotifError = true;

    public boolean SingleMiddleClick = false;

    public YetAnotherConfigLib getLibConfig() {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("wecode.config"))
                .category(getMainCategory())
                .category(getNotificationsCategory())
                .save(this::save)
                .build();
    }

    private ConfigCategory getNotificationsCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("wecode.config.category.notifications"))
                .group(getDFToNotifGroup())

                .build();
    }

    private OptionGroup getDFToNotifGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("wecode.config.notifications.dftonotif"))
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("wecode.config.notifications.dftonotif.success"))
                        .description(OptionDescription.createBuilder()
                                .text(Text.translatable("wecode.config.notifications.dftonotif.success.description"))
                                .build())
                        .binding(true, () -> DFToNotifSuccess, (aBoolean -> DFToNotifSuccess = aBoolean))
                        .controller(TickBoxControllerBuilderImpl::new)
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("wecode.config.notifications.dftonotif.error"))
                        .description(OptionDescription.createBuilder()
                                .text(Text.translatable("wecode.config.notifications.dftonotif.error.description"))
                                .build())
                        .binding(true, () -> DFToNotifError, (aBoolean -> DFToNotifError = aBoolean))
                        .controller(TickBoxControllerBuilderImpl::new)
                        .build())
                .build();
    }

    private ConfigCategory getMainCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("wecode.config.category.main"))
                .option(getTemplatePeekerOption())
                .option(getMiddleClickSingleOption())
                .group(getFlightSpeedOptionGroup())
                .build();
    }

    private Option<Boolean> getTemplatePeekerOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.template_peeker"))
                .description(OptionDescription.createBuilder()
                        .text(Text.translatable("wecode.config.template_peeker.description1"),
                        Text.translatable("wecode.config.template_peeker.description2"))
                        .build())
                .binding(true, () -> TemplatePeeker, (aBoolean -> TemplatePeeker = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
    }

    private Option<Boolean> getMiddleClickSingleOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.single_middle_click"))
                .description(OptionDescription.createBuilder()
                        .text(Text.translatable("wecode.config.single_middle_click.description1"))
                        .build())
                .binding(true, () -> SingleMiddleClick, (aBoolean -> SingleMiddleClick = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
    }

    private OptionGroup getFlightSpeedOptionGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("wecode.config.flightspeed"))
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("wecode.config.flightspeed.normal"))
                        .binding(100, () -> NormalFlightSpeed, (integer -> NormalFlightSpeed = integer))
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("wecode.config.flightspeed.fast"))
                        .binding(300, () -> FastFlightSpeed, (integer -> FastFlightSpeed = integer))
                        .controller(IntegerFieldControllerBuilderImpl::new)
                        .build())
                .build();
    }

    public static Config getConfig() {
        if (instance == null) {
            try {
                instance = WeCode.GSON.fromJson(FileUtil.readConfig(), Config.class);
            } catch (Exception exception) {
                WeCode.LOGGER.info("Config failed to load: " + exception);
                WeCode.LOGGER.info("Creating new config");
                instance = new Config();
                instance.save();
                try {
                    instance = WeCode.GSON.fromJson(FileUtil.readConfig(), Config.class);
                } catch (Exception ignored) {}
            }
        }
        return instance;
    }

    public void save() {
        try {
            JsonObject object = WeCode.GSON.toJsonTree(instance).getAsJsonObject();

            FileUtil.writeConfig(object.toString());
        } catch (Exception e) {
            WeCode.LOGGER.info("Failed to save config: {}", String.valueOf(e));
        }
    }
}
