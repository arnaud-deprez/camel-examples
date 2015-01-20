package be.arndep.camel.rest.api.account;

import com.wordnik.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

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
}
