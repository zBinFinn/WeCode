package org.zbinfinn.wecode.helpers;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.util.NumberUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationHelper {
    public static void sendNotificationWithSound(RegistryEntry.Reference<SoundEvent> sound, float volume, float pitch, Text text, NotificationType notificationType, double durationSeconds) {
        sendNotificationWithSound(sound.value(),  volume, pitch, text, notificationType, durationSeconds);
    }

    public static void sendNotificationWithSound(RegistryEntry.Reference<SoundEvent> sound, Text text, NotificationType notificationType, double durationSeconds) {
        sendNotificationWithSound(sound, 1f, 1f, text, notificationType, durationSeconds);
    }

    public static void sendNotificationWithSound(SoundEvent sound, float volume, float pitch, Text text, NotificationType notificationType, double durationSeconds) {
        sendNotification(text, notificationType, durationSeconds);
        WeCode.MC.player.playSound(sound, volume, pitch);
    }

    public static void sendNotificationWithSound(SoundEvent sound, Text text, NotificationType notificationType, double durationSeconds) {
        sendNotificationWithSound(sound, 1f, 1f, text, notificationType, durationSeconds);
    }

    public static void sendFailNotification(String message, double seconds) {
        sendNotificationWithSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED, 0.5f, 1, Text.literal(message), NotificationType.MOD_ERROR, seconds);
    }

    public static void sendAppliedNotification(String message, double seconds) {
        sendNotificationWithSound(SoundEvents.ENTITY_GLOW_ITEM_FRAME_ADD_ITEM, 0.5f, 1, Text.literal(message), NotificationType.MOD_SUCCESS, seconds);
    }

    public static void sendFailNotification(Text message, double seconds) {
        sendNotificationWithSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED, 1, 1, message, NotificationType.MOD_ERROR, seconds);
    }

    public static void sendAppliedNotification(Text message, double seconds) {
        sendNotificationWithSound(SoundEvents.ENTITY_GLOW_ITEM_FRAME_ADD_ITEM, 1, 1, message, NotificationType.MOD_SUCCESS, seconds);
    }



    public enum NotificationType {
        SUCCESS(0xAA_88AA88, 0xAA_88FF88),
        NEUTRAL(0xAA_888888, 0xAA_AAAAAA),
        ERROR(0xAA_AA8888, 0xAA_FF8888),

        MOD_ERROR(0xAA_CC88AA, 0xAA_FF88CC),
        MOD_SUCCESS(0xAA_CCAACC, 0xAA_FFCCFF),
        MOD_NORMAL(0xAA_AA88AA, 0xAA_FF88FF);

        final int backgroundColor;
        final int lineColor;
        final int textColor;
        NotificationType(int backgroundColor, int lineColor, int textColor) {
            this.backgroundColor = backgroundColor;
            this.lineColor = lineColor;
            this.textColor = textColor;
        }
        NotificationType(int backgroundColor, int lineColor) {
            this(backgroundColor, lineColor, 0xFFFFFFFF);
        }
    }

    static class Notification {
        Notification(NotificationType type, Text text, int x, int y, double durationSeconds) {
            this.type = type;
            this.text = text;
            this.x = x;
            defaultX = x;
            this.y = y;
            this.totalDurationSeconds = durationSeconds;
            this.durationSecondsLeft = durationSeconds;
        }

        private double defaultX;
        private double slidePercentage = 0;
        private final NotificationType type;
        private final Text text;
        private double x;
        private double y;
        private final double totalDurationSeconds;
        private double durationSecondsLeft;
        private boolean toBeDisposed = false;

        public void tick(double dt, int index) {
            if (durationSecondsLeft <= 0) {
                slidePercentage -= dt * 3/100;
                if (slidePercentage <= 0) {
                    toBeDisposed = true;
                    slidePercentage = 0;
                }
            } else if (slidePercentage < 1) {
                slidePercentage += dt * 2/100;
                if (slidePercentage > 1) {
                    slidePercentage = 1;
                }
            }

            updateVars();
            durationSecondsLeft -= dt/100;
            double expectedX = WeCode.MC.getWindow().getScaledWidth() - BORDER_WIDTH - 4;


            int expectedY = index * (BORDER_HEIGHT + TIME_LEFT_BAR_HEIGHT + PADDING_TOP + 3) + 10;
            y = NumberUtil.lerp(y, expectedY, dt * 3);
            if (y != expectedY) {
                //return;
            }

            x = NumberUtil.hotLerp(defaultX, expectedX, slidePercentage);
        }

        private int PADDING_WIDTH;
        private int BORDER_WIDTH;
        private int PADDING_HEIGHT;
        private int BORDER_HEIGHT;
        private int TIME_LEFT_BAR_HEIGHT;
        private int PADDING_TOP;

        private void updateVars() {
            PADDING_WIDTH = 16;
            BORDER_WIDTH = WeCode.MC.textRenderer.getWidth(text) + PADDING_WIDTH;

            PADDING_TOP = 2;
            PADDING_HEIGHT = 4;
            BORDER_HEIGHT = WeCode.MC.textRenderer.fontHeight + PADDING_HEIGHT;
            TIME_LEFT_BAR_HEIGHT = 4;
        }

        public void render(DrawContext dc, int index) {
            MatrixStack stack = dc.getMatrices();
            stack.push();
            stack.translate(0, 0, 6000);

            int xI = (int) x;
            int yI = (int) y;

            int xLeft = xI - PADDING_WIDTH/2;
            int xRight = xI + BORDER_WIDTH - PADDING_WIDTH/2;
            int yTop = yI - PADDING_HEIGHT/2 - PADDING_TOP;
            int yBottom = yI + BORDER_HEIGHT - PADDING_HEIGHT/2;

            // Background Box
            dc.fill(xLeft, yBottom, xRight, yTop, type.backgroundColor );

            // Time Left Bar
            dc.fill(xLeft, yBottom, (int) (xLeft + (xRight - xLeft) * percentageLeft()), yBottom + TIME_LEFT_BAR_HEIGHT, type.lineColor );

            stack.translate(0, 0, 0);

            dc.drawTextWithShadow(WeCode.MC.textRenderer, text, xI, yI, type.textColor);

            stack.pop();
        }

        private double percentageLeft() {
            double perc = durationSecondsLeft / totalDurationSeconds;
            if (perc < 0) {
                return 0;
            }
            return perc;
        }

        public boolean shouldDispose() {
            return toBeDisposed;
        }
    }

    private static final ArrayList<Notification> notifications = new ArrayList<>();

    public static void sendNotification(String msg, NotificationType type, double durationSeconds) {
        sendNotification(Text.literal(msg), type, durationSeconds);
    }
    public static void sendNotification(Text msg, NotificationType type, double durationSeconds) {
        notifications.addFirst(new Notification(type, msg, WeCode.MC.getWindow().getScaledWidth() + 10, 10, durationSeconds));
    }

    public static void render(DrawContext draw, RenderTickCounter tickCounter) {
        var deltaTime = tickCounter.getTickDelta(true);
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);

            notification.tick(deltaTime, i);
            notification.render(draw, i);
        }

        Set<Notification> toBeRemoved = new HashSet<>();
        notifications.stream().filter(Notification::shouldDispose).forEach(toBeRemoved::add);
        toBeRemoved.forEach(notifications::remove);
    }

    public static void sendNotification(String msg, double durationSeconds) {
        sendNotification(msg, NotificationType.NEUTRAL, durationSeconds);
    }
}
