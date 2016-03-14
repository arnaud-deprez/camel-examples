package be.arndep.camel.account.impl.services

import be.arndep.camel.account.api.AccountCommandService
import be.arndep.camel.account.api.AccountType
import be.arndep.camel.account.api.CreateAccount
import be.arndep.camel.account.impl.domain.BankAccount
import be.arndep.camel.account.impl.domain.BankAccountRepository
import be.arndep.camel.account.api.exceptions.BankAccountException
import be.arndep.camel.account.api.exceptions.BankAccountNotFoundException
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static be.arndep.camel.account.impl.BankAccountUtils.*
/**
 * Created by arnaud.deprez on 14/03/16.
 */
class BankAccountCommandServiceTest extends Specification {
    BankAccountRepository repository
    AccountCommandService service

    def setup() {
        repository = Mock()
        service = new BankAccountServiceImpl(repository)
    }

    @Unroll
    def "Call to create with a CreateAccount command should"() {
        given: "A CreateAccount command"
        def owner = "test"
        def accountType = AccountType.BLUE
        def openedDate = LocalDateTime.now()
        CreateAccount createAccount = CreateAccount.builder(accountType, owner).build()

        when: "Call create"
        def result = service.create(createAccount)

        then: "A create operation is invoked on the repository"
        1 * repository.create(_) >> { BankAccount b ->
            BankAccount.builder()
                .from(b)
                .balance(0D)
                .id(1L)
                .openedDate(openedDate)
                .build()
        }
        0 * _
        and: "the result should match the command"
        assert result.id == 1L
        assert result.owner == owner
        assert result.accountType == accountType
        assert result.balance == 0D
        assert result.openedDate == openedDate
        assert result.closedDate == Optional.empty()
    }

    @Unroll
    def "Call to withdraw with an existing account's id and an amount should"() {
        given: "An existing account"
        def bankAccount = createBankAccount(1L, "owner1", balance, AccountType.BLUE)

        when: "A withdraw operation is invoked"
        def result = service.withdraw(1L, withdrawAmount)

        then: "A find and update operation are invoked on the repository"
        1 * repository.find(_) >> Optional.of(bankAccount)
        1 * repository.update(_) >> bankAccount
        0 * _
        and: "The is withdraw of the $withdrawAmount"
        assert result.balance == newBalance

        where:
        balance || withdrawAmount | newBalance
        50D     || 50D            | 0D
        100D    || 20D            | 80D
    }

    @Unroll
    def "Call to withdraw with an existing account's id and an amount > balance should"() {
        given: "An existing account"
        def bankAccount = createBankAccount(1L, "owner1", 0D, AccountType.BLUE)

        when: "A withdraw operation is invoked"
        def result = service.withdraw(1L, 20D)

        then: "A find and update operation are invoked on the repository"
        1 * repository.find(_) >> Optional.of(bankAccount)
        0 * _
        and: "A BankAccountException is thrown"
        thrown(BankAccountException)
    }

    @Unroll
    def "Call to withdraw with an unknown id should"() {
        when: "We call withdraw"
        service.withdraw(1L, 20D)

        then: "A call to find with the same id is done on the $repository"
        1 * repository.find(1L) >> Optional.empty()
        0 * _
        and: "A BankAccountNotFoundException is thrown"
        thrown(BankAccountNotFoundException)
    }

    @Unroll
    def "Call to deposit with an existing account's id and an amount should"() {
        given: "An existing account"
        def bankAccount = createBankAccount(1L, "owner1", balance, AccountType.BLUE)

        when: "A deposit operation is invoked"
        def result = service.deposit(1L, depositAmount)

        then: "A find and update operation are invoked on the repository"
        1 * repository.find(_) >> Optional.of(bankAccount)
        1 * repository.update(_) >> bankAccount
        0 * _
        and: "The account is deposited of the $depositAmount"
        assert result.balance == newBalance

        where:
        balance || depositAmount | newBalance
        0D      || 50D           | 50D
        100D    || 20D           | 120D
    }

    @Unroll
    def "Call to deposit with an unknown id should"() {
        when: "We call deposit"
        service.deposit(1L, 20D)

        then: "A call to find with the same id is done on the $repository"
        1 * repository.find(1L) >> Optional.empty()
        0 * _
        and: "A BankAccountNotFoundException is thrown"
        thrown(BankAccountNotFoundException)
    }

    @Unroll
    def "Call to transfer with 2 existing account's id and an amount should"() {
        given: "2 existing accounts"
        def a1 = createBankAccount(1L, "owner1", 100D, AccountType.BLUE)
        def a2 = createBankAccount(2L, "owner2", 20D, AccountType.BLUE)

        when: "A transfer operation is invoked"
        def result = service.transfer(1L, 2L, transferAmount)

        then: "A find and update operation are invoked on the repository"
        1 * repository.find(1L) >> Optional.of(a1)
        1 * repository.find(2L) >> Optional.of(a2)
        1 * repository.update(a1) >> a1
        1 * repository.update(a2) >> a2
        0 * _
        and: "The transfer should withdraw the first account an deposit the second account"
        assert result.size() == 2
        assert result[0].balance == newB1
        assert result[1].balance == newB2

        where:
        transferAmount | newB1 | newB2
        20D            | 80D   | 40D
    }

    @Unroll
    def "Call to transfer with an unknown id should"() {
        when: "We call transfer"
        service.transfer(1L, 2L, 20D)

        then: "A call to find with the same id is done on the $repository"
        1 * repository.find(1L) >> a1
        (_..1) * repository.find(2L) >> a2
        0 * _
        and: "A BankAccountNotFoundException is thrown"
        thrown(BankAccountNotFoundException)

        where:
        a1                                                                  | a2
        Optional.empty()                                                    | Optional.of(createBankAccount(1L, "owner", 100D, AccountType.BLUE))
        Optional.of(createBankAccount(1L, "owner", 100D, AccountType.BLUE)) | Optional.empty()
        Optional.empty()                                                    | Optional.empty()
    }
}