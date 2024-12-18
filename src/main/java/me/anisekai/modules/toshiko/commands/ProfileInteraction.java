package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.chiya.services.data.UserDataService;
import me.anisekai.modules.toshiko.Texts;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class ProfileInteraction {

    private final UserDataService service;

    public ProfileInteraction(UserDataService service) {

        this.service = service;
    }

    // <editor-fold desc="@ profile [user: ?user, icon: ?string, active: ?boolean, admin: ?boolean, web: ?boolean]">
    @Interact(
            name = "profile",
            description = Texts.PROFILE_DESCRIPTION,
            options = {
                    @Option(
                            name = "user",
                            description = Texts.PROFILE__OPTION_USER,
                            type = OptionType.USER
                    ),
                    @Option(
                            name = "icon",
                            description = Texts.PROFILE__OPTION_ICON,
                            type = OptionType.STRING
                    ),
                    @Option(
                            name = "active",
                            description = Texts.PROFILE__OPTION_ACTIVE,
                            type = OptionType.BOOLEAN
                    ),
                    @Option(
                            name = "admin",
                            description = Texts.PROFILE__OPTION_ADMIN,
                            type = OptionType.BOOLEAN
                    ),
                    @Option(
                            name = "web",
                            description = Texts.PROFILE__OPTION_WEB,
                            type = OptionType.BOOLEAN
                    )
            }
    )
    public SlashResponse runProfile(
            DiscordUser sender,
            @Param("user") User user,
            @Param("icon") String icon,
            @Param("active") Boolean active,
            @Param("admin") Boolean admin,
            @Param("web") Boolean web
    ) {

        if ((user != null || admin != null || active != null || web != null) && !sender.isAdmin()) {
            return new SimpleResponse("Seul un administrateur peut modifier ces informations.", false, false);
        }

        if (icon == null && active == null && admin == null && web == null) {
            return new SimpleResponse("Aucune information actualisée.", false, false);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        long         targetId   = user != null ? user.getIdLong() : sender.getId();
        IMentionable targetUser = UserSnowflake.fromId(targetId);

        this.service.getProxy().modify(targetId, target -> {

            if (admin != null) {
                target.setAdmin(admin);
                embedBuilder.addField("Statut administrateur", admin ? "Administrateur" : "Membre", false);
            }

            if (active != null) {
                target.setActive(active);
                embedBuilder.addField("Statut d'activité", active ? "Actif" : "Non actif", false);
            }

            if (web != null) {
                target.setWebAccess(web);
                embedBuilder.addField("Accès Web", web ? "Autorisé" : "Non autorisé", false);
            }

            if (icon != null) {

                if (this.service.isEmoteInUse(icon)) {
                    embedBuilder.addField("Icône de vote", target.getEmote() + " *(Inchangé car déjà utilisé)*", false);
                } else {
                    target.setEmote(icon);
                    embedBuilder.addField(
                            "Icône de vote",
                            icon,
                            false
                    );
                }
            }
        });

        embedBuilder.setDescription("Modification(s) effectuée(s) sur l'utilisateur " + targetUser.getAsMention());
        return new SimpleResponse(embedBuilder, false, false);
    }
    // </editor-fold>

}
