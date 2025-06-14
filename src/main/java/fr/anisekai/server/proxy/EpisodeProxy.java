package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.adapters.EpisodeEventAdapter;
import fr.anisekai.server.events.EpisodeCreatedEvent;
import fr.anisekai.server.exceptions.episode.EpisodeNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.EpisodeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class EpisodeProxy extends ProxyService<Episode, Long, EpisodeEventAdapter, EpisodeRepository> {

    public EpisodeProxy(ApplicationEventPublisher publisher, EpisodeRepository repository) {

        super(publisher, repository, Episode::new);
    }

    /**
     * Same as {@link #fetchEntity(Function)} but should ensure that the selector should not return any empty optional
     * instance by throwing any {@link RuntimeException} using {@link Optional#orElseThrow(Supplier)}.
     *
     * @param selector
     *         The selector to use to retrieve the entity.
     *
     * @return The entity instance.
     */
    @Override
    public Episode getEntity(Function<EpisodeRepository, Optional<Episode>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(EpisodeNotFoundException::new);
    }

    public Episode create(Consumer<EpisodeEventAdapter> consumer) {

        return this.create(consumer, EpisodeCreatedEvent::new);
    }

}
