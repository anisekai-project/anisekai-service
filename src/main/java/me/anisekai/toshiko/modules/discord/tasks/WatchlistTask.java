package me.anisekai.toshiko.modules.discord.tasks;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.CronState;
import me.anisekai.toshiko.modules.discord.messages.embeds.WatchlistEmbed;
import me.anisekai.toshiko.services.WatchlistService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WatchlistTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistTask.class);

    private final WatchlistService service;
    private final RankingHandler   ranking;
    private final Watchlist        watchlist;
    private final TextChannel      channel;

    public WatchlistTask(WatchlistService service, RankingHandler ranking, Watchlist watchlist, TextChannel channel) {

        this.service   = service;
        this.ranking   = ranking;
        this.watchlist = watchlist;
        this.channel   = channel;
    }

    @Override
    public String getName() {

        return String.format("WATCHLIST:%s", this.watchlist.getStatus().name());
    }

    public void run() {

        LOGGER.info("Running for watchlist {}", this.watchlist.getStatus().name());

        boolean shouldCreateMessage = this.watchlist.getMessageId() == null;

        ButtonResponse    message         = new WatchlistEmbed(this.watchlist, this.ranking);
        Optional<Message> existingMessage = this.findExistingMessage(this.watchlist);


        if (shouldCreateMessage || existingMessage.isEmpty()) {
            LOGGER.info(
                    "Sending a new message (Has ID: {}, Has MSG: {})",
                    !shouldCreateMessage,
                    existingMessage.isPresent()
            );
            MessageCreateBuilder mcb = new MessageCreateBuilder();
            message.getHandler().accept(mcb);

            Message discordMessage = this.channel.sendMessage(mcb.build()).complete();
            this.watchlist.setMessageId(discordMessage.getIdLong());

        } else {
            MessageEditBuilder meb = new MessageEditBuilder();
            message.getHandler().accept(meb);

            existingMessage.get().editMessage(meb.build()).complete();
        }

        this.watchlist.setState(CronState.DONE);
        this.service.getRepository().save(this.watchlist);
    }

    private Optional<Message> findExistingMessage(Watchlist watchlist) {

        if (watchlist.getMessageId() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.channel.retrieveMessageById(watchlist.getMessageId()).complete());
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                return Optional.empty();
            }
            throw e;
        }
    }

}