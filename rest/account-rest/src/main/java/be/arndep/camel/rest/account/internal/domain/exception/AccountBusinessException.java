package be.arndep.camel.rest.account.internal.domain.exception;

/**
 * Created by arnaud on 22/03/15.
 */
public class AccountBusinessException extends Exception {
	public AccountBusinessException() {
		super();
	}

	public AccountBusinessException(String message) {
		super(message);
	}

	public AccountBusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountBusinessException(Throwable cause) {
		super(cause);
	}
}
