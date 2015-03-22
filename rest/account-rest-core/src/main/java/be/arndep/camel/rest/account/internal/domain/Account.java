package be.arndep.camel.rest.account.internal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Created by arnaud on 30.12.14.
 */
@Getter
@ToString
@Builder
public class Account {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Double balance;
	private String owner;

    public Account deposit(final Double amount) {
        balance += amount;
        this.lastModifiedDate = LocalDateTime.now();
        return this;
    }
    
    public Account withdraw(final Double amount) {
		this.balance -= amount;
		this.lastModifiedDate = LocalDateTime.now();
        return this;
    }

	public Account update(final AccountUpdateData data) {
		this.owner = data.newOwner;
		return this;
	}

	public boolean isPositive() {
		return this.getBalance() > 0D;
	}

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Account)) return false;
        return ((Account)obj).getId().equals(this.getId());
    }

	@Builder
	@Getter
	@ToString
	public static final class AccountUpdateData {
		private final String newOwner;
	}
}
