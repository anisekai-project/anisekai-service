package me.anisekai.toshiko.events.user;

import me.anisekai.toshiko.entities.DiscordUser;
import org.springframework.context.ApplicationEvent;

public class UserActivityUpdatedEvent extends ApplicationEvent {

    private final DiscordUser user;

    public UserActivityUpdatedEvent(Object source, DiscordUser user) {

        super(source);
        this.user = user;
    }

    public DiscordUser getUser() {

        return this.user;
    }
}