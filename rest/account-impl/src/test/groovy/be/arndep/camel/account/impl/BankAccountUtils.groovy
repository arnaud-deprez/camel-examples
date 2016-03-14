package be.arndep.camel.account.impl

import be.arndep.camel.account.api.AccountType
import be.arndep.camel.account.impl.domain.BankAccount

import java.time.LocalDateTime
import java.util.stream.Collectors

/**
 * Created by arnaud.deprez on 21/03/16.
 */
class BankAccountUtils {
    def static createBankAccount(Long id, String owner, Double balance, AccountType accountType) {
        BankAccount.builder()
                .id(id)
                .owner(owner)
                .accountType(accountType)
                .balance(balance)
                .openedDate(LocalDateTime.now())
                .build()
    }

    def static createRandomListOfBankAccount(limit) {
        (1..limit.orElse(20)).stream()
                .map {
            i -> createBankAccount(i, "owner$i", i, AccountType.values()[i%3])
        }.collect(Collectors.toList())
    }
}
