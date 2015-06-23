package be.arndep.camel.account.core.domain;

import be.arndep.camel.account.api.AccountType;
import be.arndep.camel.account.core.domain.exceptions.BankAccountException;
import be.arndep.camel.account.core.domain.specifications.WithdrawableSpecification;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by arnaud on 23/06/15.
 */
@ToString
public class BankAccount extends Identity<Long> {
	private AccountType accountType;
	private LocalDateTime openedDate;
	private Optional<LocalDateTime> closedDate;
	private Double balance;
	private String owner;
	private WithdrawableSpecification withdrawableSpecification;

	//region Consutrctors
	private BankAccount() {
		this.accountType = AccountType.BLUE;
		this.openedDate = LocalDateTime.now();
		this.closedDate = Optional.empty();
		this.balance = new Double(0);
		this.withdrawableSpecification = new WithdrawableSpecification();
	}

	private BankAccount(final Builder builder) {
		this();
		setId(Optional.ofNullable(builder.id).orElse(this.getId()));
		accountType = Optional.ofNullable(builder.accountType).orElse(this.accountType);
		openedDate = Optional.ofNullable(builder.openedDate).orElse(this.openedDate);
		closedDate = Optional.ofNullable(builder.closedDate).orElse(this.closedDate);
		balance = Optional.ofNullable(builder.balance).orElse(this.balance);
		owner = Optional.ofNullable(builder.owner).orElse(this.owner);
	}
	//endregion

	//region public methods
	public BankAccount deposit(final Double amount) {
		balance += amount;
		return this;
	}

	public boolean isWithdrawAble() {
		return withdrawableSpecification.test(this);
	}

	public BankAccount withdraw(final Double amount) throws BankAccountException {
		if (!isWithdrawAble()) {
			throw new BankAccountException("Account.withdraw is illegal according to withdrawableSpecification for: " + this);
		}
		this.balance -= amount;
		return this;
	}

	public BankAccount transfer(final BankAccount to, final Double amount) throws BankAccountException {
		this.withdraw(amount);
		to.deposit(amount);
		return this;
	}

	public void close() {
		this.closedDate = Optional.of(LocalDateTime.now());
	}

	public boolean isClosed() {
		return this.closedDate.isPresent();
	}
	//endregion

	//region getters

	public AccountType getAccountType() {
		return accountType;
	}

	public LocalDateTime getOpenedDate() {
		return openedDate;
	}

	public Optional<LocalDateTime> getClosedDate() {
		return closedDate;
	}

	public Double getBalance() {
		return balance;
	}

	public String getOwner() {
		return owner;
	}

	//endregion


	//region static factory method
	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(final BankAccount copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		builder.accountType = copy.accountType;
		builder.openedDate = copy.openedDate;
		builder.closedDate = copy.closedDate;
		builder.balance = copy.balance;
		builder.owner = copy.owner;
		return builder;
	}
	//endregion

	//region Builder
	public static final class Builder {
		private Long id;
		private AccountType accountType;
		private LocalDateTime openedDate;
		private Optional<LocalDateTime> closedDate;
		private Double balance;
		private String owner;

		private Builder() {
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder accountType(AccountType accountType) {
			this.accountType = accountType;
			return this;
		}

		public Builder openedDate(LocalDateTime openedDate) {
			this.openedDate = openedDate;
			return this;
		}

		public Builder closedDate(Optional<LocalDateTime> closedDate) {
			this.closedDate = closedDate;
			return this;
		}

		public Builder balance(Double balance) {
			this.balance = balance;
			return this;
		}

		public Builder owner(String owner) {
			this.owner = owner;
			return this;
		}

		public BankAccount build() {
			return new BankAccount(this);
		}
	}
	//endregion
}
