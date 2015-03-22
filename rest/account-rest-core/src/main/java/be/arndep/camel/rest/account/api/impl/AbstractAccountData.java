package be.arndep.camel.rest.account.api.impl;

import be.arndep.camel.rest.account.api.AccountData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by arnaud on 22/03/15.
 */
public abstract class AbstractAccountData implements AccountData {
	private final Double balance;
	private final String owner;

	@JsonCreator
	public AbstractAccountData(
			@JsonProperty("balance") final Double balance,
			@JsonProperty("owner") final String owner) {
		this.balance = balance;
		this.owner = owner;
	}

	@Override
	public Double getBalance() {
		return balance;
	}

	@Override
	public String getOwner() {
		return owner;
	}
}
