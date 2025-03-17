package org.zbinfinn.wecode.config;

import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.impl.controller.DoubleFieldControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.EnumControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.IntegerFieldControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.util.FileUtil;

public class Config {
    public static Config instance;

    public int NormalFlightSpeed = 100;
    public int FastFlightSpeed = 300;
    public boolean TemplatePeeker = true;
    public boolean CPUDisplay = true;
    public boolean ParameterDisplay = true;
    public boolean DFToNotifSuccess = true;
    public boolean DFToNotifError = true;
    public boolean MessageStacker = true;
    public boolean ShowTagsInDev = false;
    public double DefaultNotificationDuration = 5;

    public boolean AutoChatMode = false;
    public ChatMode PreferredChatMode = ChatMode.GLOBAL;

    public boolean Debug = false;

    public YetAnotherConfigLib getLibConfig() {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("wecode.config"))
                .category(getMainCategory())
                .category(getDevCategory())
                .category(getNotificationsCategory())
                .save(this::save)
                .build();
    }

    private ConfigCategory getMainCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("wecode.config.category.main"))
                .group(getAutoChatGroup())
                .option(getCPUDisplayOption())
//                .option(getChatStackerOption()) Legacy
                .group(getFlightSpeedOptionGroup())
                .option(getDebugOption())
                .build();
    }

    private OptionGroup getAutoChatGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("wecode.config.category.autochat"))
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("wecode.config.autochat.enabled"))
                        .binding(false, () -> AutoChatMode, (aBoolean -> AutoChatMode = aBoolean))
                        .controller(TickBoxControllerBuilderImpl::new)
                        .build())
                .option(Option.createBuilder(ChatMode.class)
                        .name(Text.translatable("wecode.config.autochat.preferred_mode"))
                        .binding(ChatMode.GLOBAL, () -> PreferredChatMode, aMode -> PreferredChatMode = aMode)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(ChatMode.class))
                        .build())


                .build();
    }

    private ConfigCategory getDevCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("wecode.config.category.dev"))

                .option(getParameterDisplayOption())
                .option(getTemplatePeekerOption())
                .option(getAlwaysShowTagsInDevOption())

                .build();
    }

    private Option<Boolean> getAlwaysShowTagsInDevOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.always_show_tags_in_dev"))
                .binding(true, () -> ShowTagsInDev, (aBoolean -> ShowTagsInDev = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
    }

    private ConfigCategory getNotificationsCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("wecode.config.category.notifications"))
                .option(getDefaultNotificationDurationOption())
                .group(getDFToNotifGroup())
                .build();
    }

    private Option<Double> getDefaultNotificationDurationOption() {
        return Option.createBuilder(double.class)
                .name(Text.translatable("wecode.config.default_notification_duration"))
                .binding(5d, () -> DefaultNotificationDuration, (aDouble -> DefaultNotificationDuration = aDouble))
                .controller(DoubleFieldControllerBuilderImpl::new)
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

    private Option<Boolean> getDebugOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.debug"))
                .binding(true, () -> Debug, (aBoolean -> Debug = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
    }

    private Option<Boolean> getChatStackerOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.message_stacker"))
                .binding(true, () -> MessageStacker, (aBoolean -> MessageStacker = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
    }

    private Option<Boolean> getParameterDisplayOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.parameter_display"))
                .binding(true, () -> ParameterDisplay, (aBoolean -> ParameterDisplay = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
    }

    private Option<Boolean> getCPUDisplayOption() {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable("wecode.config.cpu_display"))
                .binding(true, () -> CPUDisplay, (aBoolean -> CPUDisplay = aBoolean))
                .controller(TickBoxControllerBuilderImpl::new)
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
