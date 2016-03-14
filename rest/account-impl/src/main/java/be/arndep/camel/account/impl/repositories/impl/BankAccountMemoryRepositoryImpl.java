package be.arndep.camel.account.impl.repositories.impl;


import be.arndep.camel.account.impl.domain.BankAccount;
import be.arndep.camel.account.impl.domain.BankAccountRepository;

import java.time.LocalDateTime;
import java.util.*;
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
				BankAccount.builder()
						.owner("Arnaud")
						.balance(100_000D)
						.openedDate(LocalDateTime.now())
						.id(sequence.longValue())
						.build()
		);
		sequence.increment();
		accounts.putIfAbsent(
				sequence.longValue(),
				BankAccount.builder()
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
        BankAccount a = BankAccount.builder()
				.from(bankAccount)
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
				.map(Map.Entry::getValue);
    }

    @Override
    public List<BankAccount> findAll(final Optional<Integer> page, final Optional<Integer> limit) {
        return accounts.values()
				.stream()
                .skip(page.orElse(0) * limit.orElse(20))
                .limit(limit.orElse(20))
				.sorted(Comparator.comparingLong(BankAccount::getId))
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
                .filter(b -> b)
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
