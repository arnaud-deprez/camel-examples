package be.arndep.camel.account.core.services.mapper;


import be.arndep.camel.account.api.ReadAccount;
import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.shared.mapper.Mapper;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 22/03/15.
 */
public class ReadAccountsMapper implements Mapper<Collection<BankAccount>, Collection<ReadAccount>> {
	private final ReadAccountMapper readAccountMapper;

	public ReadAccountsMapper() {
		readAccountMapper = new ReadAccountMapper();
	}

	@Override
	public Collection<ReadAccount> map(final Collection<BankAccount> accounts) {
		return accounts.stream()
				.map(readAccountMapper)
				.collect(Collectors.toList());
	}
}
