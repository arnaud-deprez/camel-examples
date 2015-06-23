package be.arndep.camel.account.core.domain

import be.arndep.camel.account.api.AccountType
import be.arndep.camel.account.core.domain.exceptions.BankAccountException
import spock.lang.Specification

/**
 * Created by arnaud on 24/06/15.
 */
class BankAccountTest extends Specification {
    def "Deposit an amount on an account should increase the balance of the account by this amount"() {
        given:
        def a = new BankAccount(accountType: AccountType.BLUE, owner: "Arnaud", balance: initialBalance)

        when:
        a.deposit(amount)

        then:
        a.getClosedDate().isPresent() == false
        a.isClosed() == false
        a.getBalance() == balance

        where:
        initialBalance  | amount      || balance
        0               | 0           || 0
        0               | 1000        || 1000
        500             | 0           || 500
        500             | 1000        || 1500
    }

    def "Withdraw an amount on an account should decrease the balance of the account by this amount"() {
        given:
        def a = new BankAccount(accountType: AccountType.BLUE, owner: "Arnaud", balance: initialBalance)

        when:
        a.withdraw(amount)

        then:
        a.getClosedDate().isPresent() == false
        a.isClosed() == false
        a.getBalance() == balance

        where:
        initialBalance  | amount    || balance
        500             | 200       || 300
        500             | 500       || 0
    }

    def "Withdraw an amount on an account without enough money should throw an exception"() {
        given:
        def a = new BankAccount(accountType: accountType, owner: "Arnaud", balance: initialBalance)

        when:
        a.withdraw(amount)

        then:
        a.getClosedDate().isPresent() == false
        a.isClosed() == false
        final BankAccountException e = thrown()
        e.getMessage().startsWith("Account.withdraw is illegal according to withdrawableSpecification for: ")

        where:
        accountType         | initialBalance | amount    || balance
        AccountType.BLUE    | 0              | 100       || 300
        AccountType.RED     | -1000          | 100       || 0
        AccountType.GOLD    | -2500          | 100       || 0
    }

    def "Transfer an amount from an account a to an account b should withdraw a by the amount and deposit b by the same amount"() {
        given:
        def a = new BankAccount(accountType: AccountType.GOLD, owner: "Paul")
        def b = new BankAccount(accountType: AccountType.BLUE, owner: "Arnaud")

        when:
        a.transfer(b, amount)

        then:
        a.getClosedDate().isPresent() == false
        a.isClosed() == false
        b.getClosedDate().isPresent() == false
        b.isClosed() == false
        a.getBalance() == balanceA
        b.getBalance() == balanceB

        where:
        amount    || balanceA  | balanceB
        500       || -500      | 500
    }

    def "Close an account should set a closedDate and return true by calling the isClosed method"() {
        given:
        def a = new BankAccount(accountType: AccountType.GOLD, owner: "Paul")

        when:
        a.close()

        then:
        a.getClosedDate().isPresent() == true
        a.isClosed() == true
    }
}
