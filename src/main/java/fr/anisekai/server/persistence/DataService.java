package fr.anisekai.server.persistence;

import fr.anisekai.wireless.api.persistence.interfaces.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class allowing you to define common logic of data modification for an entity.
 *
 * @param <E>
 *         Type of the entity
 * @param <ID>
 *         Type of the entity's ID
 * @param <I>
 *         Type of the entity's main interface
 * @param <R>
 *         Type of the repository
 * @param <P>
 *         Type of the proxy service
 */
public class DataService<E extends I, ID extends Serializable, I extends Entity<ID>, R extends JpaRepository<E, ID>, P extends ProxyService<E, ID, I, R>> {

    private final P proxy;

    public DataService(P proxy) {

        this.proxy = proxy;
    }

    public P getProxy() {

        return this.proxy;
    }

    /**
     * Retrieve the entity from the provided id.
     *
     * @param id
     *         The entity's id.
     *
     * @return The entity matching the id.
     */
    public E fetch(ID id) {

        return this.proxy.getEntity(id);
    }

    /**
     * Retrieve the entity from the provided selector.
     *
     * @param selector
     *         The repository selector.
     *
     * @return The entity returned by the selector.
     */
    public E fetch(Function<R, Optional<E>> selector) {

        return this.proxy.getEntity(selector);
    }

    /**
     * Retrieve all entities from the provided collection of ids.
     *
     * @param ids
     *         The collection of ids.
     *
     * @return The collection of entities.
     */
    public List<E> fetchAll(Iterable<ID> ids) {

        return this.fetchAll(repository -> repository.findAllById(ids));
    }

    /**
     * Retrieve all entities from the provided selector.
     *
     * @param selector
     *         The repository selector.
     *
     * @return The collection of entities.
     */
    public List<E> fetchAll(Function<R, List<E>> selector) {

        return this.proxy.fetchEntities(selector);
    }

    /**
     * Retrieve all entities.
     *
     * @return The collection of entities
     */
    public List<E> fetchAll() {

        return this.fetchAll(ListCrudRepository::findAll);
    }

    /**
     * Modify the entity matching the id using the provided consumer.
     *
     * @param id
     *         The entity's id.
     * @param consumer
     *         Consumer allowing to modify the entity.
     *
     * @return The updated entity.
     */
    public E mod(ID id, Consumer<I> consumer) {

        return this.mod(id, Collections.singleton(consumer));
    }

    /**
     * Modify the entity matching the id using the provided collection of consumers.
     *
     * @param id
     *         The entity's id.
     * @param consumers
     *         Consumers allowing to modify the entity.
     *
     * @return The updated entity.
     */
    public E mod(ID id, Iterable<Consumer<I>> consumers) {

        return this.proxy.modify(id, entity -> consumers.forEach(consumer -> consumer.accept(entity)));
    }

    /**
     * Modify the entity returned by the selector using the provided consumer.
     *
     * @param selector
     *         The repository selector.
     * @param consumer
     *         Consumer allowing to modify the entity.
     *
     * @return The updated entity.
     */
    public E mod(Function<R, Optional<E>> selector, Consumer<I> consumer) {

        return this.mod(selector, Collections.singleton(consumer));
    }

    /**
     * Modify the entity returned by the selector using the provided collection of consumer.
     *
     * @param selector
     *         The repository selector.
     * @param consumers
     *         Consumers allowing to modify the entity.
     *
     * @return The updated entity.
     */
    public E mod(Function<R, Optional<E>> selector, Iterable<Consumer<I>> consumers) {

        return this.proxy.modify(selector, entity -> consumers.forEach(consumer -> consumer.accept(entity)));
    }

    /**
     * Modify the entities returned by the selector using the provided consumer.
     *
     * @param selector
     *         The repository selector.
     * @param consumer
     *         Consumer allowing to modify the entity.
     *
     * @return The updated entity.
     */
    public List<E> batch(Function<R, List<E>> selector, Consumer<I> consumer) {

        return this.batch(selector, Collections.singleton(consumer));
    }

    /**
     * Modify the entities returned by the selector using the provided collection of consumer.
     *
     * @param selector
     *         The repository selector.
     * @param consumers
     *         Consumers allowing to modify the entity.
     *
     * @return The updated entity.
     */
    public List<E> batch(Function<R, List<E>> selector, Iterable<Consumer<I>> consumers) {

        return this.proxy.batch(
                selector, entities -> {
                    entities.forEach(entity -> consumers.forEach(consumer -> consumer.accept(entity)));
                }
        );
    }


}
