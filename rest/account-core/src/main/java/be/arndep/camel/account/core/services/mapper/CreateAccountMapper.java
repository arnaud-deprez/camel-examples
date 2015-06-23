package be.arndep.camel.account.core.services.mapper;

import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.shared.mapper.Mapper;

/**
 * Created by arnaud on 22/03/15.
 */
public class CreateAccountMapper implements Mapper<CreateAccount, BankAccount> {
	@Override
	public BankAccount map(final CreateAccount createAccount) {
		return BankAccount.newBuilder()
				.accountType(createAccount.getAccountType())
				.owner(createAccount.getOwner())
				.build();
	}
}
