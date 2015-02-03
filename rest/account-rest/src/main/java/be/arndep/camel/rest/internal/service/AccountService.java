package be.arndep.camel.rest.internal.service;

import be.arndep.camel.rest.api.account.*;
import be.arndep.camel.rest.internal.apiImpl.ReadAccountImpl;
import be.arndep.camel.rest.internal.apiImpl.ReadAccountsImpl;
import be.arndep.camel.rest.internal.domain.account.Account;
import be.arndep.camel.rest.internal.domain.account.AccountRepository;
import be.arndep.camel.shared.rest.Link;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 30.12.14.
 */
public class AccountService {
	private static final String SELF = "self";
	
	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public ReadAccounts findAll(Long page, Long limit) {
		Optional<Long> optionalPage = Optional.ofNullable(page);
		Optional<Long> optionalLimit = Optional.ofNullable(limit);
		List<ReadAccount> accounts = accountRepository.readAll(optionalPage, optionalLimit).stream()
				.map(this::mapToReadAccount)
				.collect(Collectors.toList());
		
		return new ReadAccountsImpl(accounts, 
			Link.newBuilder()
				.href("/accounts")
				.rel(SELF)
				.withTypes(MediaType.APPLICATION_JSON)
				.withMethods("GET", "POST")
				.build()
		);
	}

	public ReadAccount create(CreateAccount account) {
		return Optional.of(accountRepository.create(mapToAccount(account)))
				.map(this::mapToReadAccount)
				.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
	}

	public ReadAccount get(Long id) {
		return accountRepository.read(id)
				.map(this::mapToReadAccount)
				.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
	}

	public ReadAccount update(Long id, UpdateAccount account) {
		return accountRepository.read(id)
				.map(a -> Account.builder()
								.id(a.getId())
								.createdDate(a.getCreatedDate())
								.balance(account.getBalance())
								.closed(account.isClosed())
								.build()
				)
				.map(a -> accountRepository.update(a))
				.map(this::mapToReadAccount)
				.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
	}

	public ReadAccount delete(Long id) {
		Optional<Account> result = accountRepository.read(id);
		result.ifPresent(a -> accountRepository.delete(a));
		return mapToReadAccount(result.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND)));
	}

	public ReadAccount deposit(Long id, Double amount) {
		return accountRepository.read(id)
				.map(a -> a.deposit(amount))
				.map(a -> accountRepository.update(a))
				.map(this::mapToReadAccount)
				.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
	}

	public ReadAccount withdraw(Long id, Double amount) {
		return accountRepository.read(id)
				.map(a -> a.withdraw(amount))
				.map(a -> accountRepository.update(a))
				.map(this::mapToReadAccount)
				.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
	}

	public ReadAccount transfer(Long id, TransferData transferData) {
		Account from = accountRepository.read(id)
				.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
		Account to = accountRepository.read(transferData.getTo())
				.orElseThrow(() -> new WebApplicationException(Response.Status.BAD_REQUEST));

		from.transfer(transferData.getAmount(), to);
		accountRepository.update(from);
		accountRepository.update(to);
		return mapToReadAccount(from);
	}

	private final ReadAccount mapToReadAccount(final Account a) {
		Collection<Link> links = new ArrayList<>();
		links.add(Link.newBuilder().href("/accounts/{0}").rel(SELF).withTypes(MediaType.APPLICATION_JSON).withMethods("GET, PUT, DELETE").build(a.getId()));
		links.add(Link.newBuilder().href("/accounts/{0}/deposit").rel("deposit").withTypes(MediaType.APPLICATION_JSON).withMethods("POST").build(a.getId()));
		if (a.isPositive()) {
			links.add(Link.newBuilder().href("/accounts/{0}/withdraw").rel("withdraw").withTypes(MediaType.APPLICATION_JSON).withMethods("POST").build(a.getId()));
			links.add(Link.newBuilder().href("/accounts/{0}/transfer").rel("transfer").withTypes(MediaType.APPLICATION_JSON).withMethods("POST").build(a.getId()));
		}
		links.add(Link.newBuilder().href("/accounts").rel("collection").withTypes(MediaType.APPLICATION_JSON).withMethods("GET", "POST").build(a.getId()));
		return new ReadAccountImpl(a, links);
	}

	private final Account mapToAccount(final CreateAccount a) {
		return Account.builder()
				.balance(a.getBalance())
				.closed(false)
				.build();
	}
}
