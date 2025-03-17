package org.zbinfinn.wecode.plotdata;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.PacketSender;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.helpers.NotificationHelper;
import org.zbinfinn.wecode.plotdata.linestarters.Event;
import org.zbinfinn.wecode.plotdata.linestarters.Function;
import org.zbinfinn.wecode.plotdata.linestarters.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PlotDataManager {
    private enum LineStarterCachingState {
        INACTIVE,
        FUNCTIONS,
        PROCESSES,
        EVENTS,
    }

    private static LineStarterCachingState lineStarterCachingState = LineStarterCachingState.INACTIVE;
    private static long timeoutTime;
    private static ArrayList<LineStarter> lineStarters = new ArrayList<>();
    private static ArrayList<LineStarter> lineStartersCache;

    public static void init() {

    }

    public static void cacheLineStarters() {
        lineStartersCache = new ArrayList<>();

        sendCompletionPacket("ctp function ");
        lineStarterCachingState = LineStarterCachingState.FUNCTIONS;
        timeoutTime = System.currentTimeMillis() + 5000;
    }

    private static void sendCompletionPacket(String command) {
        RequestCommandCompletionsC2SPacket functionsPacket = new RequestCommandCompletionsC2SPacket(69420, command);
        PacketSender.sendPacket(functionsPacket);
    }

    public static boolean receivePacket(Packet<?> packet) {
        if (lineStarterCachingState == LineStarterCachingState.INACTIVE) {
            return false;
        }
        if (System.currentTimeMillis() > timeoutTime) {
            lineStarterCachingState = LineStarterCachingState.INACTIVE;
            NotificationHelper.sendFailNotification("Plot Function Caching Timed Out", 3);
            return false;
        }
        if (!(packet instanceof CommandSuggestionsS2CPacket(int id, int start, int length, List<CommandSuggestionsS2CPacket.Suggestion> suggestions))) {
            return false;
        }
        for (CommandSuggestionsS2CPacket.Suggestion suggestion : suggestions) {
            String name = suggestion.text();
            lineStartersCache.add(switch (lineStarterCachingState) {
                case EVENTS -> event(name);
                case PROCESSES -> proc(name);
                case FUNCTIONS -> func(name);
                default -> null;
            });
        }
        switch (lineStarterCachingState) {
            case FUNCTIONS -> {
                sendCompletionPacket("ctp process ");
                lineStarterCachingState = LineStarterCachingState.PROCESSES;
                break;
            }
            case PROCESSES -> {
                sendCompletionPacket("ctp event ");
                lineStarterCachingState = LineStarterCachingState.EVENTS;
                break;
            }
            case EVENTS -> {
                lineStarterCachingState = LineStarterCachingState.INACTIVE;
                lineStartersCache.sort((ls1, ls2) -> ls1.getName().toLowerCase().compareTo(ls2.getName().toLowerCase()));
                lineStarters = lineStartersCache;
                break;
            }
            default -> {
                break;
            }
        }

        return true;
    }

    private static Event event(String name) {
        return new Event(name);
    }

    private static Function func(String functionName) {
        return new Function(functionName);
    }

    private static Process proc(String processName) {
        return new Process(processName);
    }

    public static List<LineStarter> getLineStarters() {
        return lineStarters;
    }
}
