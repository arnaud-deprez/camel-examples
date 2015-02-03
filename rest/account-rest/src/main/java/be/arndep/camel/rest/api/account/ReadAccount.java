package be.arndep.camel.rest.api.account;

import be.arndep.camel.shared.rest.Link;
import com.wordnik.swagger.annotations.ApiModel;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by arnaud on 16/01/15.
 */
@ApiModel(parent = AccountData.class)
public interface ReadAccount extends AccountData {
	//Inheritance doesn't work well with camel, so we need to rewrite the method.
	Double getBalance();

	LocalDateTime getCreatedDate();
	LocalDateTime getLastModifiedDate();
	boolean isClosed();

	Collection<Link> getLinks();
}
