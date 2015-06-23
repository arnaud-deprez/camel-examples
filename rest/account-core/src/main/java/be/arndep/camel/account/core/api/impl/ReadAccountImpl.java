package be.arndep.camel.account.core.api.impl;

import be.arndep.camel.account.api.ReadAccount;
import be.arndep.camel.account.core.domain.BankAccount;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by arnaud on 23/06/15.
 */
public class ReadAccountImpl extends AbstractAccountData implements ReadAccount {
	private final Long id;
	private final LocalDateTime openedDate;
	private final Optional<LocalDateTime> closedDate;
	private final Double balance;
	private final Boolean withdrawAble;

	public ReadAccountImpl(final BankAccount bankAccount) {
		super(bankAccount.getOwner(), bankAccount.getAccountType());
		this.id = bankAccount.getId();
		this.openedDate = bankAccount.getOpenedDate();
		this.closedDate = bankAccount.getClosedDate();
		this.balance = bankAccount.getBalance();
		this.withdrawAble = bankAccount.isWithdrawAble();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public LocalDateTime getOpenedDate() {
		return openedDate;
	}

	@Override
	public LocalDateTime getClosedDate() {
		return closedDate.orElse(null);
	}

	@Override
	public Double getBalance() {
		return balance;
	}

	@Override
	public Boolean isWithdrawAble() {
		return withdrawAble;
	}
}
