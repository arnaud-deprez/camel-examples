package be.arndep.camel.transaction.jpa.message;

import be.arndep.camel.shared.repository.CrudRepository;

/**
 * Created by arnaud on 03.01.15.
 */
public interface MessageRepository extends CrudRepository<Message, Long> {
}
