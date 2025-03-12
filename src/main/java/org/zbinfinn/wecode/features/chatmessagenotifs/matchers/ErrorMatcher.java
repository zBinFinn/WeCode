package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import org.intellij.lang.annotations.RegExp;
import org.zbinfinn.wecode.features.chatmessagenotifs.SuperMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.error.GenericErrorMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import static org.zbinfinn.wecode.features.chatmessagenotifs.matchers.GenericMatcher.gen;
import static org.zbinfinn.wecode.features.chatmessagenotifs.matchers.LambdaGenericMatcher.gen;

public class ErrorMatcher extends SuperMatcher {
    public ErrorMatcher() {
        genericMatches.add("That node is currently offline.");
        genericMatches.add("That node is currently full.");
        matchers.add(gen("Chat is currently muted. This is likely due to a staff promotion or event.", "Chat is currently muted."));
        matchers.add(gen("You are currently muted and cannot speak.", "L Bozo"));
        genericMatches.add("Uh oh! Something went wrong.");
        genericMatches.add("The targeted location is not inside your plot.");
        matchers.add(gen("You have reached this plot's decoration limit! This plot can only have up to //d+ decorations.", (string) -> {
            return "You can only have " +
                    string.replace("You have reached this plot's decoration limit! This plot can only have up to ", "").replace(" decorations.", "") +
                    "decorations on this plot.";
        }));
        genericMatches.add("You already own this product!");
        matchers.add(gen("Not enough space! Try continuing the line in a Function and then calling it with a Call Function block!", "Not enough space!"));
        matchers.add(gen("Rows of code blocks must start with an Event, Function, or Process", "Code must start with a Linestarter"));
        matchers.add(gen("You cannot have the same event twice in one game! Add code to the existing event.", "You cannot have the same event twice in one game."));
        matchers.add(gen("This event does not exist on your plot yet. You cannot teleport to it.", "That plot does not exist on this plot."));
        matchers.add(gen("No functions found! Create a function using the Function block.", "No functions found!"));
        matchers.add(gen("No processes found! Create a process using the Process block.", "No processes found!"));
        matchers.add(gen("No events found! Create an event using the Entity or Player Event block.", " No events found!"));
        genericMatches.add("Unable to create code template! Exceeded the code data size limit.");
        genericMatches.add("This event cannot be cancelled automatically!");
        matchers.add(gen("There is already a .+ with that name!"));
        matchers.add(gen(".+ names must be between 1 and 100 characters. Colors are ignored."));

        // TODO: Remove when you've covered all cases
        matchers.add(new GenericErrorMatcher());
    }

    @Override
    protected String trim(String message) {
        return message.substring("Error: ".length());
    }

    @Override
    protected boolean canTrim(String message) {
        return message.startsWith("Error: ");
    }

    @Override
    public NotificationHelper.NotificationType getNotificationType() {
        return NotificationHelper.NotificationType.ERROR;
    }
}
