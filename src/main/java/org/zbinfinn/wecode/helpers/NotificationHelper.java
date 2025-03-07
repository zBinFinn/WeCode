package org.zbinfinn.wecode.helpers;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.util.NumberUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationHelper {
    public enum NotificationType {
        SUCCESS(0xAA_88AA88, 0xAA_88FF88),
        NEUTRAL(0xAA_888888, 0xAA_AAAAAA),
        ERROR(0xAA_AA8888, 0xAA_FF8888);

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
            this.y = y;
            this.totalDurationSeconds = durationSeconds;
            this.durationSecondsLeft = durationSeconds;
        }
        private final NotificationType type;
        private final Text text;
        private double x;
        private double y;
        private final double totalDurationSeconds;
        private double durationSecondsLeft;
        private boolean toBeDisposed = false;

        public void tick(double dt, int index) {
            updateVars();
            durationSecondsLeft -= dt/100;
            double expectedX = WeCode.MC.getWindow().getScaledWidth() - BORDER_WIDTH - 4;

            if (durationSecondsLeft <= 0) {
                expectedX = WeCode.MC.getWindow().getScaledWidth() + BORDER_WIDTH + 4;
                if (x == expectedX) {
                    toBeDisposed = true;
                }
            }

            x = NumberUtil.lerp(x, expectedX, dt * 5);
            if (x != expectedX) {
                return;
            }

            int expectedY = index * (BORDER_HEIGHT + TIME_LEFT_BAR_HEIGHT + PADDING_TOP + 3) + 10;
            y = NumberUtil.lerp(y, expectedY, dt * 3);
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
        notifications.addFirst(new Notification(type, msg, WeCode.MC.getWindow().getScaledWidth(), 10, durationSeconds));
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
