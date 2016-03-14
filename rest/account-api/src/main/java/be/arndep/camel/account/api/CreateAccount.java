package be.arndep.camel.account.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created by arnaud on 23/06/15.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCreateAccount.class)
@JsonDeserialize(as = ImmutableCreateAccount.class)
public interface CreateAccount extends AccountData {
	static Builder builder(final AccountType accountType, final String owner) {
		return ImmutableCreateAccount.builder()
			.accountType(accountType)
			.owner(owner);
	}

	interface Builder
		extends
		AccountData.Builder<Builder, CreateAccount>
	{
		Builder from(CreateAccount instance);
	}
}
