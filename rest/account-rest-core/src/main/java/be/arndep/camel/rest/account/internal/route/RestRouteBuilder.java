package be.arndep.camel.rest.account.internal.route;

import be.arndep.camel.rest.account.api.*;
import be.arndep.camel.rest.account.api.impl.CreateAccountImpl;
import be.arndep.camel.rest.account.api.impl.TransferImpl;
import be.arndep.camel.rest.account.api.impl.UpdateAccountImpl;
import be.arndep.camel.rest.account.internal.domain.Account;
import be.arndep.camel.rest.account.internal.domain.exception.AccountNotFoundException;
import be.arndep.camel.rest.account.internal.mapper.AccountUpdateDataMapper;
import be.arndep.camel.rest.account.internal.mapper.CreateAccountMapper;
import be.arndep.camel.rest.account.internal.mapper.ReadAccountMapper;
import be.arndep.camel.rest.account.internal.mapper.ReadAccountsMapper;
import com.fasterxml.jackson.databind.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by arnaud on 30.12.14.
 */
public class RestRouteBuilder extends RouteBuilder {
	@Override
	public void configure() throws Exception {
		onException(AccountNotFoundException.class)
				.handled(true)
				.logHandled(true)
				.logStackTrace(true)
				.transform().simple("${exception}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(Response.Status.NOT_FOUND.getStatusCode())
				.setHeader(Exchange.CONTENT_TYPE).constant(MediaType.APPLICATION_JSON)
			.end();

		onException(AccountNotFoundException.class)
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

		final ObjectMapper objectMapper = create();

		rest("/accounts")
			.description("Account rest service")
			.consumes("application/json")
			.produces("application/json")

			.get()
				.description("Get all acounts")
				.outTypeList(ReadAccount.class)
				.to("direct://accounts.find.all")

			.post()
				.description("Create a new account")
				.type(CreateAccount.class)
				.outType(ReadAccount.class)
				.to("direct://acounts.create")

			.get("/{id}")
				.description("Find account by id")
				.outType(ReadAccount.class)
				.to("direct://acounts.find.id")

			.put("/{id}")
				.description("Update an account by id")
				.type(UpdateAccount.class)
				.outType(ReadAccount.class)
				.to("direct://acounts.update")

			.delete("/{id}")
				.description("Delete an account by id")
				.outType(ReadAccount.class)
				.to("direct://acounts.delete")

			.post("/{id}/deposit")
				.description("Add money to the account")
				.type(BigDecimal.class)
				.outType(ReadAccount.class)
				.to("direct://acounts.deposit")

			.post("/{id}/withdraw")
				.description("Withdraw money to the account")
				.type(BigDecimal.class)
				.outType(ReadAccount.class)
				.to("direct://acounts.withdraw")

			.post("/{id}/transfer")
				.description("Transfer money from the current account to another account")
				.type(Transfer.class)
				.outType(ReadAccount.class)
				.to("direct://acounts.transfer");

		from("direct://accounts.find.all")
				.id("accounts-find-all")
				.to("bean:accountService?method=readAll(${header.page},${header.limit})")
				.transform().method(ReadAccountsMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccounts.class));


		from("direct://acounts.create")
				.id("accouts-create")
				.unmarshal(new JacksonDataFormat(objectMapper, CreateAccountImpl.class))
				.transform().method(CreateAccountMapper.class)
				.to("bean:accountService?method=create(${body})")
				.transform().method(ReadAccountMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.CREATED.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

		from("direct://acounts.find.id")
				.id("accouts-find-id")
				.to("bean:accountService?method=read(${header.id})")
				.transform().method(ReadAccountMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

		from("direct://acounts.update")
				.id("accouts-update")
				.unmarshal(new JacksonDataFormat(objectMapper, UpdateAccountImpl.class))
				.transform().method(AccountUpdateDataMapper.class)
				.to("bean:accountService?method=update(${header.id},${body})")
				.transform().method(ReadAccountMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

		from("direct://acounts.delete")
				.id("accouts-delete")
				.to("bean:accountService?method=delete(${header.id})")
				.setBody(constant(null))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON));

		from("direct://acounts.deposit")
				.id("accouts-deposit")
				.to("bean:accountService?method=deposit(${header.id},${body})")
				.transform().method(ReadAccountMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

		from("direct://acounts.withdraw")
				.id("accouts-withdraw")
				.to("bean:accountService?method=withdraw(${header.id},${body})")
				.transform().method(ReadAccountMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

		from("direct://acounts.transfer")
				.id("accouts-transfer")
				.unmarshal(new JacksonDataFormat(objectMapper, TransferImpl.class))
				.to("bean:accountService?method=transfer(${header.id},${body.to},${body.amount})")
				.transform().method(ReadAccountMapper.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));
	}

	private final ObjectMapper create() {
		ObjectMapper objectMapper = new ObjectMapper();

		SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
		serializationConfig = serializationConfig
				.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.with(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setConfig(serializationConfig);

		DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
		deserializationConfig = deserializationConfig
				.with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
		objectMapper.setConfig(deserializationConfig);

		return objectMapper;
	}
}
