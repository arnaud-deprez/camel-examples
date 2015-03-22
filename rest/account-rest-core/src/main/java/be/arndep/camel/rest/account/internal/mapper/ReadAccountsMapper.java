package be.arndep.camel.rest.account.internal.mapper;

import be.arndep.camel.rest.account.api.ReadAccounts;
import be.arndep.camel.rest.account.api.impl.ReadAccountsImpl;
import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.shared.mapper.Mapper;
import be.arndep.camel.shared.rest.Link;

import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 22/03/15.
 */
public class ReadAccountsMapper implements Mapper<Collection<Account>, ReadAccounts> {
	private final ReadAccountMapper readAccountMapper;

	public ReadAccountsMapper() {
		readAccountMapper = new ReadAccountMapper();
	}

	@Override
	public ReadAccounts map(final Collection<Account> accounts) {
		return Optional.of(
				accounts.stream()
						.map(readAccountMapper)
						.collect(Collectors.toList()))
				.map(list ->
						new ReadAccountsImpl(list,
								Link.newBuilder()
										.href("/accounts")
										.rel(Link.SELF_REL)
										.withTypes(MediaType.APPLICATION_JSON)
										.withMethods("GET", "POST")
										.build()
						))
				.get();
	}
}
