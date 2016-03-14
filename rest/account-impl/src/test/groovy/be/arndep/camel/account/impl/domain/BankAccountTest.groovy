package be.arndep.camel.account.impl.domain

import be.arndep.camel.account.api.AccountType
import be.arndep.camel.account.api.exceptions.BankAccountException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by arnaud on 24/06/15.
 */
class BankAccountTest extends Specification {
    @Unroll
    def "Deposit an amount on an account should"() {
        given:
        def a = new BankAccount(accountType: AccountType.BLUE, owner: "Arnaud", balance: initialBalance)

        when:
        a.deposit(amount)

        then: "The balance is increased by this amount"
        assert a.getBalance() == balance
        and: "The account shouldn't be closed"
        assert !a.isClosed()

        where:
        initialBalance  | amount      || balance
        0D              | 0D          || 0D
        0D              | 1000D       || 1000D
        500D            | 0D          || 500D
        500D            | 1000D       || 1500D
    }

    @Unroll
    def "Withdraw an amount on an account should"() {
        given:
        def a = new BankAccount(accountType: AccountType.BLUE, owner: "Arnaud", balance: initialBalance)

        when:
        a.withdraw(amount)

        then: "The balance is decreased by this amount"
        assert a.isWithdrawAble() == (a.balance != 0)
        assert a.getBalance() == balance
        and: "The account should not be closed"
        assert !a.isClosed()

        where:
        initialBalance  | amount    || balance
        500D            | 200D      || 300D
        500D            | 500D      || 0D
    }

    @Unroll
    def "Withdraw an amount on an account without enough money should"() {
        given:
        def a = new BankAccount(accountType: accountType, owner: "Arnaud", balance: initialBalance)

        when:
        a.withdraw(amount)

        then: "A BankAccountException is thrown"
        final BankAccountException e = thrown()
        assert e.getMessage().startsWith("Account.withdraw is illegal according to withdrawableSpecification for: ")
        and: "So the BankAccount is not withdrawable"
        assert !a.isWithdrawAble()
        and: "The account should not be closed"
        assert !a.isClosed()

        where:
        accountType         | initialBalance | amount    || balance
        AccountType.BLUE    | 0D             | 100D      || 300D
        AccountType.RED     | -1000D         | 100D      || 0D
        AccountType.GOLD    | -2500D         | 100D      || 0D
    }

    @Unroll
    def "Transfer an amount from an account a to an account b should"() {
        given:
        def a = new BankAccount(accountType: AccountType.GOLD, owner: "Paul")
        def b = new BankAccount(accountType: AccountType.BLUE, owner: "Arnaud")

        when:
        a.transfer(b, amount)

        then: "BankAccount $a is withdrawed by the $amount"
        assert a.getBalance() == balanceA
        and: "BankAccount $b is deposited by the same $amount"
        assert b.getBalance() == balanceB
        and: "Both BankAccount aren't closed"
        assert !a.isClosed()
        assert !b.isClosed()

        where:
        amount    || balanceA  | balanceB
        500D      || -500D     | 500D
    }

    @Unroll
    def "Close an account should"() {
        given:
        def a = new BankAccount(accountType: AccountType.GOLD, owner: "Paul")

        when:
        a.close()

        then: "The BankAccount is closed"
        assert a.isClosed()
    }
}
