package be.arndep.camel.rest.account.internal.mapper;

import be.arndep.camel.rest.account.api.CreateAccount;
import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.shared.mapper.Mapper;

/**
 * Created by arnaud on 22/03/15.
 */
public class CreateAccountMapper implements Mapper<CreateAccount, Account> {
	@Override
	public Account map(final CreateAccount createAccount) {
		return Account.builder()
				.owner(createAccount.getOwner())
				.balance(createAccount.getBalance())
				.build();
	}
}
