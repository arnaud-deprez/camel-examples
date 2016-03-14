package be.arndep.camel.account.api.exceptions;

/**
 * Created by arnaud on 01/07/15.
 */
public class BankAccountNotFoundException extends BankAccountException {
	public BankAccountNotFoundException() {
		super();
	}

	public BankAccountNotFoundException(String message) {
		super(message);
	}

	public BankAccountNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BankAccountNotFoundException(Throwable cause) {
		super(cause);
	}
}
