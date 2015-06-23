package be.arndep.camel.account.core.services;

import be.arndep.camel.account.core.services.mapper.CreateAccountMapper;
import be.arndep.camel.account.core.services.mapper.ReadAccountMapper;
import be.arndep.camel.account.core.services.mapper.ReadAccountsMapper;
import org.apache.camel.builder.RouteBuilder;

import java.util.Optional;

/**
 * Created by arnaud on 23/06/15.
 */
public class BankAccountServiceRoute extends RouteBuilder {
	public static final String ACCOUNT_FIND_ALL = "direct://accounts.find.all";
	public static final String ACCOUNT_CREATE = "direct://accounts.create";
	public static final String ACCOUNT_FIND_ID = "direct://accounts.find.id";
	public static final String ACCOUNT_CLOSE = "direct://accounts.close";
	public static final String ACCOUNT_DEPOSIT = "direct://accounts.deposit";
	public static final String ACCOUNT_WITHDRAW = "direct://accounts.withdraw";
	public static final String ACCOUNT_TRANSFER = "direct://accounts.transfer";

	@Override
	public void configure() throws Exception {
		from(ACCOUNT_FIND_ALL)
				.routeId("accounts-find-all")
				.process(e -> {
					e.getIn().setHeader("page", Optional.ofNullable(e.getIn().getHeader("page")));
					e.getIn().setHeader("limit", Optional.ofNullable(e.getIn().getHeader("limit")));
				})
				.to("bean:bankAccountRepository?method=findAll(${header.page},${header.limit})")
				.transform().method(ReadAccountsMapper.class, "map");

		from(ACCOUNT_CREATE)
				.routeId("accouts-create")
				.to("bean-validator:createAccount")
				.transform().method(CreateAccountMapper.class, "map")
				.to("bean:bankAccountRepository?method=create(${body})")
				.transform().method(ReadAccountMapper.class, "map");

		from(ACCOUNT_FIND_ID)
				.routeId("accouts-find-id")
				.to("bean:bankAccountService?method=find(${header.id})")
				.transform().method(ReadAccountMapper.class, "map");

		from(ACCOUNT_CLOSE)
				.routeId("accouts-close")
				.to("bean:bankAccountService?method=close(${header.id})")
				.transform().method(ReadAccountMapper.class, "map");

		from(ACCOUNT_DEPOSIT)
				.routeId("accouts-deposit")
				.to("bean:bankAccountService?method=deposit(${header.id},${body})")
				.transform().method(ReadAccountMapper.class, "map");

		from(ACCOUNT_WITHDRAW)
				.routeId("accouts-withdraw")
				.to("bean:bankAccountService?method=withdraw(${header.id},${body})")
				.transform().method(ReadAccountMapper.class, "map");

		from(ACCOUNT_TRANSFER)
				.routeId("accouts-transfer")
				.to("bean:bankAccountService?method=transfer(${header.from},${header.to},${body})")
				.transform().method(ReadAccountMapper.class, "map");
	}
}
