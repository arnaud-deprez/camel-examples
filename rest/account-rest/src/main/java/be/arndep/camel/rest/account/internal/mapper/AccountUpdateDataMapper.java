package be.arndep.camel.rest.account.internal.mapper;

import be.arndep.camel.rest.account.api.UpdateAccount;
import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.shared.mapper.Mapper;

/**
 * Created by arnaud on 22/03/15.
 */
public class AccountUpdateDataMapper implements Mapper<UpdateAccount, Account.AccountUpdateData> {
	@Override
	public Account.AccountUpdateData map(final UpdateAccount updateAccount) {
		return Account.AccountUpdateData.builder()
				.newOwner(updateAccount.getOwner())
				.build();
	}
}
