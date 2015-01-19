package be.arndep.camel.rest.api.account.impl;

import be.arndep.camel.rest.api.account.ReadAccount;
import lombok.experimental.Builder;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDateTime;

/**
 * Created by arnaud on 30.12.14.
 */
@Builder
public class ReadAccountImpl extends ResourceSupport implements ReadAccount{
    private long id;
    private Double balance;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private boolean closed;

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
}
