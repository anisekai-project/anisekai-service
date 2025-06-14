package fr.anisekai.server.events;

import fr.anisekai.server.entities.Anime;

public class AnimeUpdatedEvent<V> extends EntityUpdatedEventAdapter<Anime, V> {

    public AnimeUpdatedEvent(Object source, Anime entity, V oldValue, V newValue) {

        super(source, entity, oldValue, newValue);
    }

}
