package me.anisekai.toshiko.exceptions.broadcast;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import me.anisekai.toshiko.utils.BroadcastUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BroadcastOverlappingException extends RuntimeException implements DiscordEmbeddable {


    private final Anime          anime;
    private final AnimeNightMeta conflictWith;
    private final ZonedDateTime  starting;

    public BroadcastOverlappingException(Anime anime, AnimeNightMeta conflictWith, ZonedDateTime starting) {

        this.anime        = anime;
        this.conflictWith = conflictWith;
        this.starting     = starting;
    }

    @Override
    public EmbedBuilder asEmbed() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

        return new EmbedBuilder()
                .setTitle("Conflit de séance !")
                .setDescription(String.format(
                        "Impossible de planifier la séance pour l'anime **%s** le **%s** car cela enterait en conflit avec la séance de de l'anime **%s** (%s) programmée le **%s** et se terminant le **%s**",
                        this.anime.getName(),
                        this.starting.format(dtf),
                        this.conflictWith.getAnime().getName(),
                        BroadcastUtils.asEpisodeDescription(this.conflictWith, false),
                        this.conflictWith.getStartDateTime().format(dtf),
                        this.conflictWith.getEndDateTime().format(dtf)
                ))
                .setColor(Color.ORANGE);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }

}