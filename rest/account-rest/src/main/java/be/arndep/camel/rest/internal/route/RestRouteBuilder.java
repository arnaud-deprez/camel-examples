package be.arndep.camel.rest.internal.route;

import be.arndep.camel.rest.api.account.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Created by arnaud on 30.12.14.
 */
public class RestRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        final ObjectMapper objectMapper = create();

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.off)
                .dataFormatProperty("prettyPrint", "true");

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
                    .type(TransferData.class)
					.outType(ReadAccount.class)
                    .to("direct://acounts.transfer");

        from("direct://accounts.find.all")
                .id("accounts-find-all")
                .to("bean:accountService?method=findAll(${header.page},${header.limit})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccounts.class));

        from("direct://acounts.create")
                .id("accouts-create")
                .unmarshal(new JacksonDataFormat(objectMapper, CreateAccount.class))
                .to("bean:accountService?method=create(${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.CREATED.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

        from("direct://acounts.find.id")
                .id("accouts-find-id")
                .to("bean:accountService?method=get(${header.id})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

        from("direct://acounts.update")
                .id("accouts-update")
                .unmarshal(new JacksonDataFormat(objectMapper, UpdateAccount.class))
                .to("bean:accountService?method=update(${header.id},${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

        from("direct://acounts.delete")
                .id("accouts-delete")
                .to("bean:accountService?method=delete(${header.id})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
				.marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

        from("direct://acounts.deposit")
                .id("accouts-deposit")
                .unmarshal(new JacksonDataFormat(objectMapper, BigDecimal.class))
                .to("bean:accountService?method=deposit(${header.id},${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

        from("direct://acounts.withdraw")
                .id("accouts-withdraw")
                .unmarshal(new JacksonDataFormat(objectMapper, BigDecimal.class))
                .to("bean:accountService?method=withdraw(${header.id},${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));

        from("direct://acounts.transfer")
                .id("accouts-transfer")
                .unmarshal(new JacksonDataFormat(objectMapper, TransferData.class))
                .to("bean:accountService?method=transfer(${header.id},${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.OK.getStatusCode()))
                .marshal(new JacksonDataFormat(objectMapper, ReadAccount.class));
    }

    private final ObjectMapper create() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new MrBeanModule());

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
