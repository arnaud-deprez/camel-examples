package be.arndep.camel.account.core.api.impl;

import be.arndep.camel.account.api.AccountType;
import be.arndep.camel.account.api.CreateAccount;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Created by arnaud on 23/06/15.
 */
@JsonDeserialize(builder = CreateAccountImpl.CreateAccountBuilderImpl.class)
public class CreateAccountImpl extends AbstractAccountData implements CreateAccount {

	private CreateAccountImpl(final String owner,
							  final AccountType accountType) {
		super(owner, accountType);
	}

	private CreateAccountImpl(final CreateAccountBuilderImpl builder) {
		this(builder.owner, builder.accountType);
	}

	public static Builder newBuilder() {
		return new CreateAccountBuilderImpl();
	}

	@JsonPOJOBuilder(withPrefix = "")
	static class CreateAccountBuilderImpl implements CreateAccount.Builder {
		private CreateAccountBuilderImpl() {

		}

		private String owner;
		private AccountType accountType;

		@Override
		public Builder accountType(AccountType accountType) {
			this.accountType = accountType;
			return self();
		}

		@Override
		public Builder owner(String owner) {
			this.owner = owner;
			return self();
		}

		@Override
		public Builder self() {
			return this;
		}

		@Override
		public CreateAccountImpl build() {
			return new CreateAccountImpl(this);
		}
	}
}
