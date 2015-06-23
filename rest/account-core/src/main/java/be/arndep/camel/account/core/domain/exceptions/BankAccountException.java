package be.arndep.camel.account.core.domain.exceptions;

/**
 * Created by arnaud on 23/06/15.
 */
public class BankAccountException extends RuntimeException {
	public BankAccountException() {
		super();
	}

	public BankAccountException(String message) {
		super(message);
	}

	public BankAccountException(String message, Throwable cause) {
		super(message, cause);
	}

	public BankAccountException(Throwable cause) {
		super(cause);
	}
}
