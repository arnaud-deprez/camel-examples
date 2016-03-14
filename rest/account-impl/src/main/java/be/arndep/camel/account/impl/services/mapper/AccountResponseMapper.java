package be.arndep.camel.account.impl.services.mapper;

import be.arndep.camel.account.api.AccountResponse;
import be.arndep.camel.account.impl.domain.BankAccount;
import be.arndep.camel.shared.mapper.Mapper;

/**
 * Created by arnaud on 22/03/15.
 */
public class AccountResponseMapper implements Mapper<BankAccount, AccountResponse> {
	@Override
	public AccountResponse map(final BankAccount a) {
		return AccountResponse.builder()
			.id(a.getId())
			.balance(a.getBalance())
			.openedDate(a.getOpenedDate())
			.closedDate(a.getClosedDate())
			.withdrawAble(a.isWithdrawAble())
			.accountType(a.getAccountType())
			.owner(a.getOwner())
			.build();
	}
}
