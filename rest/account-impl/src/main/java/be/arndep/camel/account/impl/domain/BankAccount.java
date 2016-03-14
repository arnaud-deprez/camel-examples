package be.arndep.camel.account.impl.domain;

import be.arndep.camel.account.api.AccountType;
import be.arndep.camel.account.api.exceptions.BankAccountException;
import be.arndep.camel.account.impl.domain.specifications.WithdrawableSpecification;
import be.arndep.camel.shared.builder.FluentBuilder;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

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
	private Predicate<BankAccount> closableSpecification;

	//region Constructors
	private BankAccount() {
		this.accountType = AccountType.BLUE;
		this.openedDate = LocalDateTime.now();
		this.closedDate = Optional.empty();
		this.balance = 0D;
		this.withdrawableSpecification = new WithdrawableSpecification();
		this.closableSpecification = b -> b.getBalance() == 0;
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
	synchronized public BankAccount deposit(final Double amount) {
		if (isClosed())
			throw new BankAccountException("Cannot deposit money on a closed account!");
		balance += amount;
		return this;
	}

	synchronized public boolean isWithdrawAble() {
		return withdrawableSpecification.test(this);
	}

	synchronized public BankAccount withdraw(final Double amount) throws BankAccountException {
		if (!isWithdrawAble()) {
			throw new BankAccountException("Account.withdraw is illegal according to withdrawableSpecification for: " + this);
		}
		this.balance -= amount;
		return this;
	}

	/**
	 * Transfer money from one account to another
	 * <bold>WARNING:</bold> this situation might cause a deadlock when thread 1 want to transfer money from account A to account B
	 * and when another thread 2 want to transfer money from account B to account A at the same time.
	 * One way to solve this issue is to use the Akka actor model.
	 * As this project is a simple POC and this case might not occur for our demo, I'll let it as it as it's not the purpose of this sample.
	 * @param to
	 * @param amount
	 * @return
	 */
	synchronized public BankAccount transfer(final BankAccount to, final Double amount) {
		this.withdraw(amount);
		to.deposit(amount);
		return this;
	}

	synchronized public BankAccount close() {
		if (!isClosable())
			throw new BankAccountException("An account can't be closed with a balance amount != 0, value:" + this);

		this.closedDate = Optional.of(LocalDateTime.now());
		return this;
	}

	synchronized public boolean isClosed() {
		return this.getClosedDate().isPresent();
	}

	synchronized public boolean isClosable() {
		return this.closableSpecification.test(this);
	}
	//endregion

	//region getters

	synchronized public AccountType getAccountType() {
		return accountType;
	}

	synchronized public LocalDateTime getOpenedDate() {
		return openedDate;
	}

	synchronized public Optional<LocalDateTime> getClosedDate() {
		return closedDate;
	}

	synchronized public Double getBalance() {
		return balance;
	}

	synchronized public String getOwner() {
		return owner;
	}

	//endregion

	//region static factory method
	public static Builder builder() {
		return new Builder();
	}
	//endregion

	//region Builder
	public static final class Builder
		implements
		FluentBuilder<BankAccount>,
		FluentBuilder.Copy<BankAccount, Builder>
	{
		private Long id;
		private AccountType accountType;
		private LocalDateTime openedDate;
		private Optional<LocalDateTime> closedDate;
		private Double balance;
		private String owner;

		private Builder() {
		}

		public Builder from(BankAccount from) {
			this.id = from.getId();
			this.accountType = from.accountType;
			this.openedDate = from.openedDate;
			this.closedDate = from.closedDate;
			this.balance = from.balance;
			this.owner = from.owner;
			return this;
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
