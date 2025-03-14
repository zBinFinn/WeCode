package org.zbinfinn.wecode.suggestionproviders;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StringSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    private String[] suggestions;
    public StringSuggestionProvider(String[] suggestions) {
        this.suggestions = suggestions;
    }
    public StringSuggestionProvider(Collection<String> suggestions) {
        this.suggestions = suggestions.toArray(new String[0]);
    }


    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> commandContext, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (String suggestion : suggestions) {
            builder.suggest(suggestion);
        }

        return builder.buildFuture();
    }
}
