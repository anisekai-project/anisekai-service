package me.anisekai.toshiko.modules.discord.tasks;

import io.sentry.Sentry;
import me.anisekai.toshiko.modules.discord.Texts;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.helpers.FileDownloader;
import me.anisekai.toshiko.utils.BroadcastUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.managers.ScheduledEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateBroadcastTask implements Task {

    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateBroadcastTask.class);

    private final Guild     guild;
    private final Broadcast broadcast;

    public UpdateBroadcastTask(Guild guild, Broadcast broadcast) {

        this.guild     = guild;
        this.broadcast = broadcast;
    }

    @Override
    public String getName() {

        return String.format("BROADCAST:%s:UPDATE", this.broadcast.getId());
    }

    public void run() {

        if (this.broadcast.getEventId() == null) {
            return;
        }

        ScheduledEvent event = this.guild.getScheduledEventById(this.broadcast.getEventId());

        if (event == null) {
            LOGGER.error(
                    "ScheduledEvent {} (for Broadcast {}) was not found !",
                    this.broadcast.getEventId(),
                    this.broadcast.getId()
            );
            return;
        }
        String name = Texts.truncate(this.broadcast.getAnime().getName(), ScheduledEvent.MAX_NAME_LENGTH);

        ScheduledEventManager manager = event.getManager()
                                             .setName(name)
                                             .setStartTime(this.broadcast.getStartDateTime())
                                             .setEndTime(this.broadcast.getEndDateTime())
                                             .setDescription(BroadcastUtils.asEpisodeDescription(this.broadcast));

        try {
            byte[] imgData = FileDownloader.downloadAnimeCard(this.broadcast.getAnime());
            manager.setImage(Icon.from(imgData)).complete();
        } catch (Exception e) {
            Sentry.captureException(e);
            manager.complete();
        }
    }

}
