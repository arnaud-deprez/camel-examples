package be.arndep.camel.account.core.api.impl;

import be.arndep.camel.account.api.AccountData;
import be.arndep.camel.account.api.AccountType;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by arnaud on 23/06/15.
 */
@ApiModel(subTypes = {CreateAccountImpl.class, ReadAccountImpl.class})
public abstract class AbstractAccountData implements AccountData {
	private final String owner;
	@ApiModelProperty(dataType = "string")
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
