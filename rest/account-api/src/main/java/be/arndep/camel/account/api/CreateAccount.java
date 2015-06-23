package be.arndep.camel.account.api;

import be.arndep.camel.shared.builder.FluentBuilder;

/**
 * Created by arnaud on 23/06/15.
 */
public interface CreateAccount extends AccountData {
	interface Builder extends FluentBuilder<Builder, CreateAccount> {
		Builder accountType(AccountType accountType);
		Builder owner(String owner);
	}
}
