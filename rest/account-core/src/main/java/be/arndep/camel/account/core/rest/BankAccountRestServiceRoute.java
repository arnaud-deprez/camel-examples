package be.arndep.camel.account.core.rest;

import be.arndep.camel.account.core.api.impl.CreateAccountImpl;
import be.arndep.camel.account.core.api.impl.ReadAccountImpl;
import be.arndep.camel.account.core.domain.exceptions.BankAccountException;
import be.arndep.camel.account.core.domain.exceptions.BankAccountNotFoundException;
import be.arndep.camel.account.core.rest.converter.ReadAccountResourceConverter;
import be.arndep.camel.account.core.services.BankAccountServiceRoute;
import org.apache.camel.Exchange;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.apache.camel.model.rest.RestParamType.path;

/**
 * Created by arnaud on 30/06/15.
 */
public class BankAccountRestServiceRoute extends BankAccountServiceRoute {
	@Override
	public void configure() throws Exception {
		//@formatter:off
		//Exception Handling
		onException(BankAccountNotFoundException.class)
				.handled(true)
				.logHandled(true)
				.logStackTrace(true)
				.transform().simple("${exception}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(Response.Status.NOT_FOUND.getStatusCode())
				.setHeader(Exchange.CONTENT_TYPE).constant(MediaType.APPLICATION_JSON)
				.end();

		onException(BankAccountException.class)
				.handled(true)
				.logHandled(true)
				.logStackTrace(true)
				.transform().simple("${exception}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(Response.Status.BAD_REQUEST.getStatusCode())
				.setHeader(Exchange.CONTENT_TYPE).constant(MediaType.APPLICATION_JSON)
				.end();

		onException(Exception.class)
				.handled(true)
				.logHandled(true)
				.logStackTrace(true)
				.transform().simple("${exception}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				.setHeader(Exchange.CONTENT_TYPE).constant(MediaType.APPLICATION_JSON)
				.end();

		//Rest
		rest("/bankaccounts")
				.description("Bank account rest service")
				.consumes(MediaType.APPLICATION_JSON)
				.produces(MediaType.APPLICATION_JSON)

			.get()
				.description("Get all Bank accounts")
				.outTypeList(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_FIND_ALL)
					.transform().method(ReadAccountResourceConverter.class, "toResources")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post()
				.description("Create a new Bank account")
				.type(CreateAccountImpl.class)
				.outType(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_CREATE)
					.transform().method(ReadAccountResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.CREATED.getStatusCode()))
				.endRest()

			.get("/{id}")
				.description("Find a Bank account by id")
				.param().name("id").type(path).description("The bank account's id").dataType("long").endParam()
				.outType(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_FIND_ID)
					.transform().method(ReadAccountResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.put("/{id}/close")
				.description("Close an Bank account by id")
				.param().name("id").type(path).description("The bank account's id to close").dataType("long").endParam()
				.outType(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_CLOSE)
					.transform().method(ReadAccountResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post("/{id}/deposit")
				.description("Add money to the Bank account")
				.param().name("id").type(path).description("The bank account's id to deposit").dataType("long").endParam()
				.type(BigDecimal.class)
				.outType(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_DEPOSIT)
					.transform().method(ReadAccountResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post("/{id}/withdraw")
				.description("Withdraw money to the Bank account")
				.param().name("id").type(path).description("The bank account's id to withdraw").dataType("long").endParam()
				.type(BigDecimal.class)
				.outType(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_WITHDRAW)
					.transform().method(ReadAccountResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post("/{from}/transfer/{to}")
				.description("Transfer money from one Bank account to another")
				.param().name("from").type(path).description("The bank account's id from which money is withdrawn").dataType("long").endParam()
				.param().name("to").type(path).description("The bank account's id to which money is deposited").dataType("long").endParam()
				.type(BigDecimal.class)
				.outType(ReadAccountImpl.class)
				.route()
					.to(ACCOUNT_TRANSFER)
					.transform().method(ReadAccountResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest();

		//Application Service Route
		super.configure();
		//@formatter:on
	}
}
