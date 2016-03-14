package be.arndep.camel.account.impl.services;

import be.arndep.camel.account.api.AccountCommandService;
import be.arndep.camel.account.api.AccountQueryService;
import be.arndep.camel.account.api.AccountResponse;
import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.api.exceptions.BankAccountException;
import be.arndep.camel.account.api.exceptions.BankAccountNotFoundException;
import be.arndep.camel.account.impl.domain.BankAccount;
import be.arndep.camel.account.impl.domain.BankAccountRepository;
import be.arndep.camel.account.impl.services.mapper.AccountResponseMapper;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is an application service because it contains logic with the repository
 * It's definitely not domain centric:
 */
public class BankAccountServiceImpl implements AccountQueryService, AccountCommandService{
	private final BankAccountRepository bankAccountRepository;
	private final AccountResponseMapper responseMapper;

	public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
		responseMapper = new AccountResponseMapper();
		this.bankAccountRepository = bankAccountRepository;
	}

	public AccountResponse find(final Long id) throws BankAccountNotFoundException {
		return applyFctToAccount(id, Function.identity());
	}

	public Iterable<AccountResponse> findAll(Optional<Integer> page, Optional<Integer> limit) {
		return bankAccountRepository.findAll(page, limit)
			.stream()
			.map(responseMapper)
			.collect(Collectors.toList());
	}

	public AccountResponse create(CreateAccount account) {
		return Optional.of(
			bankAccountRepository.create(
				BankAccount.builder()
					.accountType(account.getAccountType())
					.owner(account.getOwner())
					.build()))
			.map(responseMapper)
			.get();
	}

	public AccountResponse close(final Long id) throws BankAccountNotFoundException {
		return applyFctToAccount(id, a -> bankAccountRepository.update(a.close()));
	}

	public AccountResponse deposit(final Long id, final Double amount) throws BankAccountNotFoundException {
		return applyFctToAccount(id, a -> bankAccountRepository.update(a.deposit(amount)));
	}

	public AccountResponse withdraw(final Long id, final Double amount) throws BankAccountException {
		return applyFctToAccount(id, a -> bankAccountRepository.update(a.withdraw(amount)));
	}

	public Iterable<AccountResponse> transfer(final Long from, final Long to, final Double amount) {
		if (Objects.equals(from, to)) {
			throw new IllegalArgumentException(MessageFormat.format("from<{0}> == to<{1}>", from, to));
		}

		final BankAccount a1 = internalFind(from);
		final BankAccount a2 = internalFind(to);
		a1.transfer(a2, amount);
		bankAccountRepository.update(a1);
		bankAccountRepository.update(a2);

		return Stream.of(a1, a2)
			.map(responseMapper)
			.collect(Collectors.toList());
	}

	private Supplier<BankAccountNotFoundException> bankAccountNotFound(final Long id) {
		return () -> new BankAccountNotFoundException(MessageFormat.format("Account.id: {0}", id));
	}

	private BankAccount internalFind(final Long id) {
		return bankAccountRepository.find(id)
			.orElseThrow(bankAccountNotFound(id));
	}

	private AccountResponse applyFctToAccount(final Long id, final Function<BankAccount, BankAccount> fct) {
		return bankAccountRepository.find(id)
			.map(fct)
			.map(responseMapper)
			.orElseThrow(bankAccountNotFound(id));
	}
}
