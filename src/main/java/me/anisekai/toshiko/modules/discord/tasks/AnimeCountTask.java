package me.anisekai.toshiko.modules.discord.tasks;

import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.services.data.AnimeDataService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class AnimeCountTask implements Task {

    private final AnimeDataService service;
    private final TextChannel      channel;

    public AnimeCountTask(AnimeDataService service, TextChannel channel) {

        this.service = service;
        this.channel = channel;
    }

    @Override
    public String getName() {

        return "ANIME:COUNT";
    }

    @Override
    public void run() {

        List<Anime> animes = this.service.fetchAll(repository -> repository.findAllByStatusIn(AnimeStatus.getDisplayable()));

        String description = String.format("Il y a en tout %s anime(s).", animes.size());
        this.channel.getManager().setTopic(description).complete();
    }

}
