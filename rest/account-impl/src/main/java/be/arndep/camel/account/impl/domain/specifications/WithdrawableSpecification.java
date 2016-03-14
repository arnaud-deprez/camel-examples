package be.arndep.camel.account.impl.domain.specifications;

import be.arndep.camel.account.impl.domain.BankAccount;

import java.util.function.Predicate;

/**
 * Created by arnaud on 23/06/15.
 */
public class WithdrawableSpecification implements Predicate<BankAccount> {
	@Override
	public boolean test(BankAccount bankAccount) {
		switch (bankAccount.getAccountType()) {
			case BLUE: return bankAccount.getBalance() > 0;
			case RED: return bankAccount.getBalance() > -1000;
			case GOLD: return bankAccount.getBalance() > -2500;
		}
		return false;
	}
}
