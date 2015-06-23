package be.arndep.camel.account.core.repositories.impl;


import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.account.core.domain.BankAccountRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Created by arnaud on 30.12.14.
 */
public class BankAccountMemoryRepositoryImpl implements BankAccountRepository {

    private LongAdder sequence;
    private ConcurrentMap<Long, BankAccount> accounts;

    public BankAccountMemoryRepositoryImpl() {
        sequence = new LongAdder();
        accounts = new ConcurrentHashMap<>();
		sequence.increment();
        accounts.putIfAbsent(
				sequence.longValue(),
				BankAccount.newBuilder()
						.owner("Arnaud")
						.balance(100_000D)
						.openedDate(LocalDateTime.now())
						.id(sequence.longValue())
						.build()
		);
		sequence.increment();
		accounts.putIfAbsent(
				sequence.longValue(),
				BankAccount.newBuilder()
						.owner("Paul")
						.balance(2082.74D)
						.openedDate(LocalDateTime.now())
						.id(sequence.longValue())
						.build()
		);
	}

    @Override
    public BankAccount create(final BankAccount bankAccount) {
        sequence.increment();
        BankAccount a = BankAccount.newBuilder(bankAccount)
                .id(sequence.longValue())
                .build();
        accounts.putIfAbsent(a.getId(), a);
        return a;
    }

    @Override
    public Optional<BankAccount> find(final Long id) {
        return accounts.entrySet()
				.stream()
                .filter(e -> id.equals(e.getKey()))
                .findFirst()
				.map(o -> o.getValue());
    }

    @Override
    public List<BankAccount> findAll(final Optional<Long> page, final Optional<Long> limit) {
        return accounts.values()
				.stream()
                .skip(page.orElse(Long.valueOf(0)) * limit.orElse(Long.valueOf(20)))
                .limit(limit.orElse(Long.valueOf(20)))
				.sorted(Comparator.comparingLong(a -> a.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public BankAccount update(final BankAccount bankAccount) {
        //As we are in memory, we should do nothing as we play directly with reference
        return bankAccount;
    }

	@Override
	public boolean delete(final Long id) {
		return Optional.of(accounts.containsKey(id))
                .filter(b -> b == true)
                    .map(b -> accounts.remove(id) != null)
                    .orElse(false);
	}

    @Override
    public boolean delete(final BankAccount bankAccount) {
        return delete(bankAccount.getId());
    }

    @Override
    public long count() {
        return accounts.size();
    }
}
