package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.shizue.services.data.WatchlistDataService;
import me.anisekai.modules.toshiko.Texts;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import me.anisekai.modules.toshiko.utils.PermissionUtils;
import org.springframework.stereotype.Component;

@InteractionBean
@Component
public class WatchlistInteraction {

    private final WatchlistDataService service;

    public WatchlistInteraction(WatchlistDataService service) {

        this.service = service;
    }

    @Interact(
            name = "watchlist/refresh",
            description = Texts.WATCHLIST_REFRESH__DESCRIPTION
    )
    public SlashResponse runWatchlistRefresh(DiscordUser sender) {

        PermissionUtils.requirePrivileges(sender);
        this.service.refreshAll();
        return new SimpleResponse("Les listes de visionnage vont être actualisées sous peu.", false, false);
    }

}