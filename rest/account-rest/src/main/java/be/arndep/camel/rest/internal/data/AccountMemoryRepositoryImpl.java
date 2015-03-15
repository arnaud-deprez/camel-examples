package be.arndep.camel.rest.internal.data;

import be.arndep.camel.rest.internal.domain.account.Account;
import be.arndep.camel.rest.internal.domain.account.AccountRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 30.12.14.
 */
public class AccountMemoryRepositoryImpl implements AccountRepository {

    private LongAdder sequence;
    private ConcurrentMap<Long, Account> accounts;

    public AccountMemoryRepositoryImpl() {
        sequence = new LongAdder();
        accounts = new ConcurrentHashMap<>();
		sequence.increment();
        accounts.putIfAbsent(
				sequence.longValue(),
				Account.builder()
						.balance(100_000D)
						.createdDate(LocalDateTime.now())
						.lastModifiedDate(LocalDateTime.now())
						.id(sequence.longValue())
						.build()
		);
		sequence.increment();
		accounts.putIfAbsent(
				sequence.longValue(),
				Account.builder()
						.balance(2082.74D)
						.createdDate(LocalDateTime.now())
						.lastModifiedDate(LocalDateTime.now())
						.id(sequence.longValue())
						.build()
		);
	}

    @Override
    public Account create(final Account account) {
        sequence.increment();
        Account a = Account.builder()
                .balance(account.getBalance())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .id(sequence.longValue())
                .build();
        accounts.putIfAbsent(sequence.longValue(), a);
        return a;
    }

    @Override
    public Optional<Account> read(final Long id) {
        return accounts.entrySet()
				.stream()
                .filter(e -> id.equals(e.getKey()))
                .findFirst()
				.map(o -> o.getValue());
    }

    @Override
    public List<Account> readAll(final Optional<Long> page, final Optional<Long> limit) {
        return accounts.entrySet()
				.stream()
                .skip(page.orElse(Long.valueOf(0)) * limit.orElse(Long.valueOf(20)))
                .limit(limit.orElse(Long.valueOf(20)))
				.map(e -> e.getValue())
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
        boolean result = accounts.containsKey(id);
        if (result)
        	accounts.remove(id);
		return result;
    }

    @Override
    public long count() {
        return accounts.size();
    }
}
