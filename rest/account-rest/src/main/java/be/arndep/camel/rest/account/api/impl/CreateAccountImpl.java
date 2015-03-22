package be.arndep.camel.rest.account.api.impl;

import be.arndep.camel.rest.account.api.CreateAccount;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by arnaud on 22/03/15.
 */
public class CreateAccountImpl extends AbstractAccountData implements CreateAccount {
	@JsonCreator
	public CreateAccountImpl(@JsonProperty("balance") final Double balance,
							 @JsonProperty("owner") final String owner) {
		super(balance, owner);
	}
}
