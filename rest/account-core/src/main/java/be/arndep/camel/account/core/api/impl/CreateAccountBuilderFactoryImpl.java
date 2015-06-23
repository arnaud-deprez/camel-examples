package be.arndep.camel.account.core.api.impl;

import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.api.CreateAccountBuilderFactory;

/**
 * Created by arnaud on 23/06/15.
 */
public class CreateAccountBuilderFactoryImpl implements CreateAccountBuilderFactory {
	@Override
	public CreateAccount.Builder newCreateAccountBuilder() {
		return CreateAccountImpl.newBuilder();
	}
}
