package be.arndep.camel.rest.internal.domain.account;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

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
    private boolean closed;

    public Account deposit(Double amount) {
        balance += amount;
        this.lastModifiedDate = LocalDateTime.now();
        return this;
    }
    
    public Account withdraw(Double amount) {
        this.balance -= amount;
        this.lastModifiedDate = LocalDateTime.now();
        return this;
    }
    
    public Account transfer(Double amount, Account to) {
        this.withdraw(amount);
        to.deposit(amount);
        return this;
    }
    
    public void open() {
        this.closed = false;
    }
    
    public void close() {
        this.closed = true;
    }
    
    public boolean isPositive() {
        return balance > 0D;
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
}
