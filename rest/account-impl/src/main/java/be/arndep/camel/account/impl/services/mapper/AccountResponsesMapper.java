package be.arndep.camel.account.impl.services.mapper;


import be.arndep.camel.account.api.AccountResponse;
import be.arndep.camel.account.impl.domain.BankAccount;
import be.arndep.camel.shared.mapper.Mapper;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 22/03/15.
 */
public class AccountResponsesMapper implements Mapper<Collection<BankAccount>, Collection<AccountResponse>> {
	private final AccountResponseMapper accountResponseMapper;

	public AccountResponsesMapper() {
		accountResponseMapper = new AccountResponseMapper();
	}

	@Override
	public Collection<AccountResponse> map(final Collection<BankAccount> accounts) {
		return accounts.stream()
				.map(accountResponseMapper)
				.collect(Collectors.toList());
	}
}
