package be.arndep.camel.rest.account.internal.domain.exception;

import java.text.MessageFormat;

/**
 * Created by arnaud on 22/03/15.
 */
public class AccountNotFoundException extends Exception {
	public AccountNotFoundException(Long accountId) {
		super(MessageFormat.format("Account not found for id:{0}!", accountId));
	}
}
