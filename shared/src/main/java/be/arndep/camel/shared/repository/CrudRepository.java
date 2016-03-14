package be.arndep.camel.shared.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by arnaud on 30.12.14.
 */
public interface CrudRepository<T, K extends Serializable> {
    T create(T t);
    Optional<T> find(K id);
    List<T> findAll(Optional<Integer> page, Optional<Integer> limit);
    T update(T t);
    boolean delete(T t);
    boolean delete(K id);
    long count();
}
