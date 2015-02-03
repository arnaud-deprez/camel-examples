package be.arndep.camel.rest.internal.apiImpl;

import be.arndep.camel.rest.api.account.ReadAccount;
import be.arndep.camel.rest.internal.domain.account.Account;
import be.arndep.camel.shared.rest.Link;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by arnaud on 30.12.14.
 */
public class ReadAccountImpl implements ReadAccount{
    private Double balance;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private boolean closed;
    private Collection<Link> links;

    public ReadAccountImpl(final Account account, 
                           final Collection<Link> links) {
        this.createdDate = account.getCreatedDate();
        this.lastModifiedDate = account.getLastModifiedDate();
        this.balance = account.getBalance();
        this.closed = account.isClosed();
        this.links = links;
    }

    @Override
    public Double getBalance() {
        return balance;
    }

    @Override
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public Collection<Link> getLinks() {
        return links;
    }
}
