package me.anisekai.modules.shizue.exceptions.seasonalvote;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SeasonalVoteNotFoundException extends RuntimeException implements DiscordEmbeddable {

    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setDescription("Le vote de selection demandé n'a pas été trouvé.");

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
