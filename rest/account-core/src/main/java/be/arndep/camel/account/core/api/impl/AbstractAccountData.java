package be.arndep.camel.account.core.api.impl;

import be.arndep.camel.account.api.AccountData;
import be.arndep.camel.account.api.AccountType;

/**
 * Created by arnaud on 23/06/15.
 */
public abstract class AbstractAccountData implements AccountData {
	private final String owner;
	private final AccountType accountType;

	protected AbstractAccountData(final String owner, final AccountType accountType) {
		super();
		this.owner = owner;
		this.accountType = accountType;
	}


	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public AccountType getAccountType() {
		return accountType;
	}
}
