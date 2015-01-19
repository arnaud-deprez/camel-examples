package be.arndep.camel.rest.api.account;

import be.arndep.camel.rest.api.account.impl.ReadAccountImpl;
import be.arndep.camel.rest.domain.account.Account;
import be.arndep.camel.rest.domain.account.AccountRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 30.12.14.
 */
public class AccountService {
	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Resources<ReadAccountImpl> findAll(Long page, Long limit) {
		Optional<Long> optionalPage = Optional.ofNullable(page);
		Optional<Long> optionalLimit = Optional.ofNullable(limit);
		List<ReadAccountImpl> accounts = accountRepository.readAll(optionalPage, optionalLimit).stream()
				.map(this::mapToReadAccount)
				.collect(Collectors.toList());
		return new PagedResources<>(accounts, 
				new PagedResources.PageMetadata(optionalLimit.orElse(Long.valueOf(accounts.size())), optionalPage.orElse(0L), accountRepository.count()),
				new Link("/accounts").withSelfRel());
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

	public void update(Long id, UpdateAccount account) {
		accountRepository.read(id)
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
		result.map(a -> accountRepository.delete(a));
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

	private final ReadAccountImpl mapToReadAccount(final Account a) {
		ReadAccountImpl response = ReadAccountImpl.builder()
				.createdDate(a.getCreatedDate())
				.lastModifiedDate(a.getLastModifiedDate())
				.balance(a.getBalance())
				.closed(a.isClosed())
				.build();

		response.add(new Link(MessageFormat.format("/accounts/{0}", a.getId())).withSelfRel());
		response.add(new Link(MessageFormat.format("/accounts/{0}/deposit", a.getId())).withRel("deposit"));
		if (a.isPositive()) {
			response.add(new Link(MessageFormat.format("/accounts/{0}/withdraw", a.getId())).withRel("withdraw"));
			response.add(new Link(MessageFormat.format("/accounts/{0}/transfer", a.getId())).withRel("transfer"));
		}
		response.add(new Link("/accounts").withRel("/accounts"));
		return response;
	}

	private final Account mapToAccount(final CreateAccount a) {
		return Account.builder()
				.balance(a.getBalance())
				.closed(false)
				.build();
	}
}
