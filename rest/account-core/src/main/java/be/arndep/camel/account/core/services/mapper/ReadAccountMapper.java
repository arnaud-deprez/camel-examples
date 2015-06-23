package be.arndep.camel.account.core.services.mapper;


import be.arndep.camel.account.api.ReadAccount;
import be.arndep.camel.account.core.api.impl.ReadAccountImpl;
import be.arndep.camel.account.core.domain.BankAccount;
import be.arndep.camel.shared.mapper.Mapper;

/**
 * Created by arnaud on 22/03/15.
 */
public class ReadAccountMapper implements Mapper<BankAccount, ReadAccount> {
	@Override
	public ReadAccount map(final BankAccount a) {
		return new ReadAccountImpl(a);
	}
}
