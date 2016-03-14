package be.arndep.camel.account.rest;

import be.arndep.camel.account.api.AccountResponse;
import be.arndep.camel.account.api.CreateAccount;
import be.arndep.camel.account.api.exceptions.BankAccountException;
import be.arndep.camel.account.api.exceptions.BankAccountNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.apache.camel.model.rest.RestParamType.path;
import static org.apache.camel.model.rest.RestParamType.query;

/**
 * Created by arnaud on 30/06/15.
 */
public class BankAccountRestServiceRoute extends RouteBuilder {
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
				.param().name("page").defaultValue("0").type(query).description("The page' number").dataType("int").endParam()
				.param().name("limit").defaultValue("20").type(query).description("The page' max size").dataType("int").endParam()
				.outTypeList(AccountResponse.class)
				.route()
					.process(e -> {
						e.getIn().setHeader("page", Optional.ofNullable(e.getIn().getHeader("page", Integer.class)));
						e.getIn().setHeader("limit", Optional.ofNullable(e.getIn().getHeader("limit", Integer.class)));
					})
					.to("bean:accountQueryService?method=findAll(${header.page},${header.limit})")
					.transform().method(AccountResponseResourceConverter.class, "toResources")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post()
				.description("Create a new Bank account")
				.type(CreateAccount.class)
				.outType(AccountResponse.class)
				.route()
					.to("bean-validator://CreateAccount")
					.to("bean:accountCommandService?method=create(${body})")
					.transform().method(AccountResponseResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.CREATED.getStatusCode()))
				.endRest()

			.get("/{id}")
				.description("Find a Bank account by id")
				.param().name("id").type(path).description("The bank account's id").dataType("long").endParam()
				.outType(AccountResponse.class)
				.route()
					.to("bean:accountQueryService?method=find(${header.id})")
					.transform().method(AccountResponseResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.put("/{id}/close")
				.description("Close an Bank account by id")
				.param().name("id").type(path).description("The bank account's id to close").dataType("long").endParam()
				.outType(AccountResponse.class)
				.route()
					.to("bean:accountCommandService?method=close(${header.id})")
					.transform().method(AccountResponseResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post("/{id}/deposit")
				.description("Add money to the Bank account")
				.param().name("id").type(path).description("The bank account's id to deposit").dataType("long").endParam()
				.type(Double.class)
				.outType(AccountResponse.class)
				.route()
					.to("bean:accountCommandService?method=deposit(${header.id},${body})")
					.transform().method(AccountResponseResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post("/{id}/withdraw")
				.description("Withdraw money to the Bank account")
				.param().name("id").type(path).description("The bank account's id to withdraw").dataType("long").endParam()
				.type(Double.class)
				.outType(AccountResponse.class)
				.route()
					.to("bean:accountCommandService?method=withdraw(${header.id},${body})")
					.transform().method(AccountResponseResourceConverter.class, "toResource")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest()

			.post("/{from}/transfer/{to}")
				.description("Transfer money from one Bank account to another")
				.param().name("from").type(path).description("The bank account's id from which money is withdrawn").dataType("long").endParam()
				.param().name("to").type(path).description("The bank account's id to which money is deposited").dataType("long").endParam()
				.type(Double.class)
				.outTypeList(AccountResponse.class)
				.route()
					.to("bean:accountCommandService?method=transfer(${header.from},${header.to},${body})")
					.transform().method(AccountResponseResourceConverter.class, "toResources")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.endRest();
		//@formatter:on
	}
}
