package me.anisekai.toshiko.modules.discord.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.modules.discord.annotations.InteractionBean;
import me.anisekai.toshiko.modules.discord.messages.responses.SimpleResponse;
import me.anisekai.toshiko.modules.library.ToshikoFileSystem;
import org.springframework.stereotype.Component;

@InteractionBean
@Component
public class AdministrativeInteraction {

    private final ToshikoFileSystem fsService;

    public AdministrativeInteraction(ToshikoFileSystem fsService) {

        this.fsService = fsService;
    }

    @Interact(
            name = "disk/import",
            description = "\uD83D\uDD12 Import file from the automation folder"
    )
    public SlashResponse importCache(DiscordUser user) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Va t'faire foute", false, true);
        }

        int count = this.fsService.checkForAutomation();
        return new SimpleResponse(String.format("%s anime vont être importés.", count), false, true);
    }

}
