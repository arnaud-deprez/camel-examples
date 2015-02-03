package be.arndep.camel.rest.internal.data;

import be.arndep.camel.rest.internal.domain.account.Account;
import be.arndep.camel.rest.internal.domain.account.AccountRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 30.12.14.
 */
public class AccountMemoryRepositoryImpl implements AccountRepository {

    private AtomicLong sequence;
    private List<Account> accounts;

    public AccountMemoryRepositoryImpl() {
        sequence = new AtomicLong(0);
        accounts = new CopyOnWriteArrayList<>();
        accounts.add(
                Account.builder()
                        .balance(100_000D)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .id(sequence.incrementAndGet())
                        .build()
        );
        accounts.add(
                Account.builder()
                        .balance(2082.74D)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .id(sequence.incrementAndGet())
                        .build()
        );
    }

    @Override
    public Account create(final Account account) {
        Account a = Account.builder()
                .balance(account.getBalance())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .id(sequence.incrementAndGet())
                .build();
        accounts.add(a);
        return a;
    }

    @Override
    public Optional<Account> read(final Long id) {
        return accounts.stream()
                .filter(m -> id.equals(m.getId()))
                .reduce((a, b) -> a);
    }

    @Override
    public List<Account> readAll(final Optional<Long> page, final Optional<Long> limit) {
        return accounts.stream()
                .skip(page.orElse(Long.valueOf(0)) * limit.orElse(Long.valueOf(20)))
                .limit(limit.orElse(Long.valueOf(20)))
                .collect(Collectors.toList());
    }

    @Override
    public Account update(final Account account) {
        //As we are in memory, we should do nothing as we play directly with reference
        return account;
    }

    @Override
    public boolean delete(final Account account) {
        return delete(account.getId());
    }

    @Override
    public boolean delete(final Long id) {
        return accounts.removeIf(m -> id.equals(m.getId()));
    }

    @Override
    public long count() {
        return accounts.size();
    }
}
