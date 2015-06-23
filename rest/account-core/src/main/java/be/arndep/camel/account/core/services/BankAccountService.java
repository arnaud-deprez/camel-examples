package be.arndep.camel.account.core.services;

import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.account.core.domain.BankAccountRepository;
import be.arndep.camel.account.core.domain.exceptions.BankAccountException;
import be.arndep.camel.account.core.domain.exceptions.BankAccountNotFoundException;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This is an application service because it contains logic with the repository
 * It's definitely not domain centric:
 */
public class BankAccountService {
	private final BankAccountRepository bankAccountRepository;

	public BankAccountService(BankAccountRepository bankAccountRepository) {
		this.bankAccountRepository = bankAccountRepository;
	}

	public BankAccount find(final Long id) throws BankAccountNotFoundException {
		return bankAccountRepository.find(id)
				.orElseThrow(bankAccountNotFound(id));
	}

	public BankAccount close(final Long id) throws BankAccountNotFoundException {
		return Optional.of(bankAccountRepository.find(id)
				.orElseThrow(bankAccountNotFound(id)))
				.filter(a -> !a.isClosed())
				.map(a -> {
					a.close();
					return bankAccountRepository.update(a);
				})
				.orElseThrow(() -> new BankAccountException(MessageFormat.format("The Bank Account <{0}> is already closed!", id)));
	}

	public BankAccount deposit(final Long id, final Double amount) throws BankAccountNotFoundException {
		return bankAccountRepository.find(id)
				.map(a -> bankAccountRepository.update(a.deposit(amount)))
				.orElseThrow(bankAccountNotFound(id));
	}

	public BankAccount withdraw(final Long id, final Double amount) throws BankAccountException {
		return bankAccountRepository.find(id)
				.map(a -> bankAccountRepository.update(a.withdraw(amount)))
				.orElseThrow(() -> new BankAccountNotFoundException(MessageFormat.format("Account.id: {0}", id)));
	}

	public BankAccount transfer(final Long from, final Long to, final Double amount) throws BankAccountException {
		if (from == to) {
			throw new IllegalArgumentException(MessageFormat.format("from<{0}> == to<{1}>", from, to));
		}
		return bankAccountRepository.find(from)
				.flatMap(a1 -> {
							bankAccountRepository.find(to)
									.map(a2 -> {
										a1.transfer(a2, amount);
										bankAccountRepository.update(a2);
										bankAccountRepository.update(a1);
										return a2;
									}).orElseThrow(this.bankAccountNotFound(to));
							return Optional.of(a1);
						}
				)
				.orElseThrow(this.bankAccountNotFound(from));
	}

	private Supplier<BankAccountNotFoundException> bankAccountNotFound(Long id) {
		return () -> new BankAccountNotFoundException(MessageFormat.format("Account.id: {0}", id));
	}
}
