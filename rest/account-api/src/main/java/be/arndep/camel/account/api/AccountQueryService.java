package be.arndep.camel.account.api;

import java.util.Optional;

/**
 * Created by arnaud.deprez on 14/03/16.
 */
public interface AccountQueryService {
	/**
	 * Find an account by its id
	 * @param id
	 * @return
	 */
	AccountResponse find(final Long id);

	Iterable<AccountResponse> findAll(Optional<Integer> page, Optional<Integer> limit);
}
