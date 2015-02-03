package be.arndep.camel.rest.api.account;

import be.arndep.camel.shared.rest.Link;

import java.util.Collection;

/**
 * Created by arnaud on 03/02/15.
 */
public interface ReadAccounts {
	Collection<ReadAccount> getContent();
	Collection<Link> getLinks();
}
