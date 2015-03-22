package be.arndep.camel.rest.account.api.impl;

import be.arndep.camel.rest.account.api.UpdateAccount;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by arnaud on 22/03/15.
 */
public class UpdateAccountImpl implements UpdateAccount {

	private final String owner;

	@JsonCreator
	public UpdateAccountImpl(
			@JsonProperty("owner") final String owner) {
		this.owner = owner;
	}

	@Override
	public String getOwner() {
		return owner;
	}
}
