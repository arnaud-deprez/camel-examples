package be.arndep.camel.account.api;

/**
 * Created by arnaud.deprez on 14/03/16.
 */
public interface AccountCommandService {
	/**
	 * Create a new account
	 * @param account
	 * @return
	 */
	AccountResponse create(CreateAccount account);

	/**
	 * Close an account
	 * @param id
	 * @return
	 */
	AccountResponse close(final Long id);

	/**
	 * Deposit a certain amount on an account
	 * @param id
	 * @param amount
	 * @return
	 */
	AccountResponse deposit(final Long id, final Double amount);

	/**
	 * Withdraw a certain amount from an account
	 * @param id
	 * @param amount
	 * @return
	 */
	AccountResponse withdraw(final Long id, final Double amount);

	/**
	 * Transfer money from one account to another account
	 * @param from
	 * @param to
	 * @param amount
	 * @return
	 */
	Iterable<AccountResponse> transfer(final Long from, final Long to, final Double amount);
}
