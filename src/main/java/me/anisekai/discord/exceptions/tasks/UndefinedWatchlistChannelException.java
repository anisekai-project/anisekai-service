package me.anisekai.discord.exceptions.tasks;

import me.anisekai.discord.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class UndefinedWatchlistChannelException extends SilentDiscordException {

    public UndefinedWatchlistChannelException() {

        super("The watchlist channel has not been setup correctly.");
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "Problème de configuration: Le salon pour les listes de visionnage n'a pas été paramétré correctement.";
    }

}
