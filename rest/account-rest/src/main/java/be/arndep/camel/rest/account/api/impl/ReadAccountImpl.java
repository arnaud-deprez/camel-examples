package be.arndep.camel.rest.account.api.impl;

import be.arndep.camel.rest.account.api.ReadAccount;
import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.shared.rest.Link;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by arnaud on 30.12.14.
 */
public class ReadAccountImpl extends AbstractAccountData implements ReadAccount{
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Collection<Link> links;

    public ReadAccountImpl(final Account account, 
                           final Collection<Link> links) {
		super(account.getBalance(), account.getOwner());
        this.createdDate = account.getCreatedDate();
        this.lastModifiedDate = account.getLastModifiedDate();
        this.links = links;
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
    public Collection<Link> getLinks() {
        return links;
    }
}
