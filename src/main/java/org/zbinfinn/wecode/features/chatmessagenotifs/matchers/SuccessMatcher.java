package org.zbinfinn.wecode.features.chatmessagenotifs.matchers;

import org.zbinfinn.wecode.features.chatmessagenotifs.SuperMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.FlySpeedMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.JoinPlotMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.SwitchModeMatcher;
import org.zbinfinn.wecode.features.chatmessagenotifs.matchers.success.WalkSpeedMatcher;
import org.zbinfinn.wecode.helpers.NotificationHelper;

import java.util.List;

import static org.zbinfinn.wecode.features.chatmessagenotifs.matchers.GenericMatcher.gen;
import static org.zbinfinn.wecode.features.chatmessagenotifs.matchers.LambdaGenericMatcher.gen;

public class SuccessMatcher extends SuperMatcher {
    public SuccessMatcher() {
        matchers.add(new JoinPlotMatcher());
        matchers.add(new SwitchModeMatcher());
        matchers.add(new FlySpeedMatcher());
        matchers.add(new WalkSpeedMatcher());

        matchers.add(gen("Chat is now set to Do Not Disturb. Public chat and messages will be blocked. Use /chat to change it again.", "Chat: DND", 2));
        matchers.add(gen("Chat is now set to None. Public chat will be blocked. Use /chat to change it again.", "Chat: None", 2));
        matchers.add(gen("Chat is now set to Local. You will only see messages from players on your plot. Use /chat to change it again.", "Chat: Local", 2));
        matchers.add(gen("Chat is now set to Global. You will now see messages from players on your node. Use /chat to change it again.", "Chat: Global", 2));

        matchers.add(gen("Set your gamemode to %s.", 2, (string) -> "Gamemode: " + string.replace("Set your gamemode to ", "").replace(".", "")));

        matchers.add(gen("Applied the glowing effect to .+\\.", "Applied Glowing", 2));
        matchers.add(gen("Removed the glowing effect from .+\\.", "Removed Glowing", 2));
        matchers.add(gen("Removed all lore from .+\\.", "Removed All Lore", 2));
        matchers.add(gen("Applied unbreakability to .+\\.", "Applied Unbreakability", 2));
        matchers.add(gen("Removed unbreakability from .+\\.", "Removed Unbreakability", 2));
        matchers.add(gen("The tooltip of .+ is now hidden\\.", "Tooltip Hidden", 2));
        matchers.add(gen("The tooltip of .+ is now visible\\.", "Tooltip Visible", 2));

        matchers.add(gen("Container locked successfully!", "Container Locked", 2));
        matchers.add(gen("Container unlocked successfully!", "Container Unlocked", 2));

        regexMatches.addAll(List.of(
                "Sending you to .+\\.\\.\\.",
                ".+ .+ has been renamed to .+\\.",
                ".+ has been named .+\\.",
                ".+ .+ has been changed to .+\\.",
                ".+ has been set to .+\\.",
                "You will no longer receive messages from .+\\.",
                "You will now receive messages from .+\\.",
                "A total of \\d+ unique players have joined DiamondFire!",
                "You currently have ⧈ \\d+ tokens\\. Progress: .+",
                "You have been given ⧈ .+ tokens!",
                ".*You tipped .+ and received one □ token notch!",
                "Your resource pack preferences for plot .+ have been cleared!",
                "The preference .+ has been set to .+\\.",
                "Plot icon set to .+!",
                "Plot icon set to .+'s Head!",
                ".+ has been whitelisted on this plot\\.",
                ".+ has been unwhitelisted on this plot\\.",
                "Successfully renamed plot #.+ to: .+",
                "Cleared \\d+ variables matching .+\\.",
                ".+ has been banned from this plot\\.",
                ".+ has been unbanned from this plot\\.",
                "Added \\d+ new codespace layers*\\.",
                "Removed \\d+ codespace layers* below you\\.",
                "Set the plot description to: .+",
                "Updated your plot handle to .+!",
                "Cloning plot to .+",
                "Status: .+",
                "Unclaiming plot .+\\.",
                "Plot .+ has been unclaimed\\.",
                "Clearing: .+",
                "Successfully cleared selected options for plot .+\\.",
                "Product with ID '.+' created successfully!",
                "Product .+ updated!",
                "The custom model data value for your plot icon is .+\\.",
                "You have successfully boosted .+\\.",
                "Your current chat mode is .+\\. You may switch with /chat <global|local|none|dnd>",
                "Set your player time to .+\\.",
                "Set your player weather to .+\\.",
                ".+ has muted the chat\\.",
                ".+ has muted the chat due to a .+\\.",
                ".+ has unmuted the chat\\.",
                "Cosmetic .+ has been disabled\\.",
                "Cosmetic .+ has been enabled\\.",
                "Received a Light of level .+\\.",
                "Changed your held item's material to .+\\.",
                "Changed this head to .+\\.",
                "Received a head of .+\\.",
                "The power of this rocket has been set to .+\\.",
                "Set book author to .+\\.",
                "Set book title to .+\\.",
                "You have sponsored .+ (|\\d+ times) for \\d+.!"
        ));


        literalMatches.addAll(List.of(
                "Your sponsor rank has been upgraded!",
                "Your pronouns have been reset!",
                "Your preferred pronouns have been updated!",
                "Sparks updated successfully!",
                "Sparks converted successfully!",
                "Updated book generation.",
                "Unsigned book.",
                "Removed book author.",
                "A new firework effect has been added to this rocket.",
                "The firework effect of this rocket has been set.",
                "The firework effect of this firework star has been set.",
                "All firework effects have been removed from this rocket.",
                "Received a new firework rocket.",
                "Successfully imported custom skin.",
                "All cosmetics have been disabled.",
                "All cosmetics have been enabled.",
                "Successfully created image!",
                "Enabled blindness.",
                "Disabled blindness.",
                "Disabled night vision.",
                "Enabled night vision.",
                "Removed plot from favorites!",
                "Added plot to favorites!",
                "Product deleted!",
                "Your plot handle has been cleared.",
                "Your plot has successfully been cloned.",
                "Cleared the plot description.",
                "Recalculating your plot's lighting, this may take a moment.",
                "Your plot has been promoted for 6 hours!",
                "Advertisement sent successfully!",
                "Cleared this plots whitelist.",
                "This plot's whitelist was already disabled.",
                "Disabled this plot's whitelist.",
                "This plot's whitelist was already enabled.",
                "Enabled this plot's whitelist.",
                "You are currently not using the plot's resource pack.",
                "Your report has been sent to online Moderators!",
                "Flight enabled.",
                "Flight disabled.",
                "Loading the ignore list menu...",
                "Custom model data applied!",
                "This event will not be cancelled when the plot code is halted by LagSlayer.",
                "This event will now automatically be cancelled when the plot code is halted by LagSlayer."
        ));
    }

    @Override
    protected String trim(String message) {
        return message.substring("» ".length());
    }

    @Override
    protected boolean canTrim(String message) {
        return message.startsWith("» ");
    }

    @Override
    public NotificationHelper.NotificationType getNotificationType() {
        return NotificationHelper.NotificationType.SUCCESS;
    }
}
