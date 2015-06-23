package be.arndep.camel.account.api;

import javax.validation.constraints.NotNull;

/**
 * Created by arnaud on 23/06/15.
 */
public interface AccountData {
	AccountType getAccountType();
	@NotNull
	String getOwner();
}
