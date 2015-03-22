package be.arndep.camel.rest.account.api;

import be.arndep.camel.shared.rest.Link;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by arnaud on 16/01/15.
 */
public interface ReadAccount extends AccountData {
	LocalDateTime getCreatedDate();
	LocalDateTime getLastModifiedDate();

	Collection<Link> getLinks();
}
