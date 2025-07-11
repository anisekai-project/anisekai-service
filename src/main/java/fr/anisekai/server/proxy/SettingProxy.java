package fr.anisekai.server.proxy;

import fr.anisekai.server.entities.Setting;
import fr.anisekai.server.entities.adapters.SettingEventAdapter;
import fr.anisekai.server.events.SettingCreatedEvent;
import fr.anisekai.server.exceptions.setting.SettingNotFoundException;
import fr.anisekai.server.persistence.ProxyService;
import fr.anisekai.server.repositories.SettingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class SettingProxy extends ProxyService<Setting, String, SettingEventAdapter, SettingRepository> {

    public SettingProxy(ApplicationEventPublisher publisher, SettingRepository repository) {

        super(publisher, repository, Setting::new);
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
    public Setting getEntity(Function<SettingRepository, Optional<Setting>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SettingNotFoundException::new);
    }

    public Setting create(Consumer<SettingEventAdapter> consumer) {

        return this.create(consumer, SettingCreatedEvent::new);
    }

}
