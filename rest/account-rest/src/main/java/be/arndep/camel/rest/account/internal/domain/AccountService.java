package be.arndep.camel.rest.account.internal.domain;

import be.arndep.camel.rest.account.internal.domain.exception.AccountBusinessException;
import be.arndep.camel.rest.account.internal.domain.exception.AccountNotFoundException;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by arnaud on 22/03/15.
 */
public class AccountService {
	private final AccountRepository accountRepository;
	private final Predicate<Account> isWithDrawable;

	public AccountService(final AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
		isWithDrawable = a -> a.isPositive();
	}

	public Collection<Account> readAll(final Long page, final Long limit) {
		return accountRepository.readAll(Optional.ofNullable(page), Optional.ofNullable(limit));
	}

	public Account read(final Long id) throws AccountNotFoundException {
		return accountRepository.read(id)
				.orElseThrow(() -> new AccountNotFoundException(id));
	}

	public Account create(final Account account) throws AccountNotFoundException {
		return accountRepository.create(account);
	}

	public Account update(final Long accountId, final Account.AccountUpdateData data) throws AccountNotFoundException {
		Account account = accountRepository.read(accountId)
				.orElseThrow(() -> new AccountNotFoundException(accountId));
		account.update(data);
		accountRepository.update(account);
		return account;
	}

	public void delete(final Long id) {
		accountRepository.delete(id);
	}

	public Account deposit(final Long accountId, final Double amount) throws AccountBusinessException, AccountNotFoundException {
		Account account = accountRepository.read(accountId)
				.orElseThrow(() -> new AccountNotFoundException(accountId));
		account.deposit(amount);
		accountRepository.update(account);
		return account;
	}

	public Account withdraw(final Long accountId, final Double amount) throws AccountBusinessException, AccountNotFoundException {
		Account account = accountRepository.read(accountId)
				.orElseThrow(() -> new AccountNotFoundException(accountId));

		if (isWithDrawable.negate().test(account)) {
			throw new AccountBusinessException(MessageFormat.format("The account <id:{0}>, <balance:{1}> is not withDrawable!",
					account.getId(), account.getBalance()));
		}

		account.withdraw(amount);
		accountRepository.update(account);
		return account;
	}

	public Account transfer(final Long fromId, final Long toId, final Double amount) throws AccountBusinessException, AccountNotFoundException {
		Account from = accountRepository.read(fromId)
				.orElseThrow(() -> new AccountNotFoundException(fromId));
		Account to = accountRepository.read(toId)
				.orElseThrow(() -> new AccountNotFoundException(toId));

		if (isWithDrawable.negate().test(from)) {
			throw new AccountBusinessException(MessageFormat.format("The account <id:{0}>, <balance:{1}> is not withDrawable!",
					from.getId(), from.getBalance()));
		}

		from.withdraw(amount);
		to.deposit(amount);
		accountRepository.update(from);
		accountRepository.update(to);
		return from;
	}
}
