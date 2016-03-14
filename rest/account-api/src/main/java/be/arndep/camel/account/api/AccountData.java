package be.arndep.camel.account.api;

import be.arndep.camel.shared.builder.FluentBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by arnaud on 23/06/15.
 */
public interface AccountData {
	@NotNull
	AccountType getAccountType();
	@NotNull
	@Size(min = 1)
	String getOwner();

	interface Builder<B extends Builder<B, R>, R extends AccountData>
		extends
		FluentBuilder<R> {
		B accountType(AccountType accountType);
		B owner(String owner);
	}
}
