package be.arndep.camel.transaction.internal;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;

/**
 * Created by arnaud on 15/02/15.
 */
public class TransactionalRouteBuilder extends RouteBuilder {
	
	public static final String SQL_ENDPOINT = "sql://INSERT INTO MESSAGE (content) VALUES (:#${body})";
	
	@Override
	public void configure() throws Exception {
		from("activemq:queue://message.in.1")
				.id("transactionalRoute-1")
				.transacted()
				.to(SQL_ENDPOINT)
				.to("activemq:queue://message.out.1");

		from("activemq:queue://message.in.2")
				.id("transactionalRoute-2")
				.transacted()
				.throwException(new IOException("Forced Exception 2. This should provoke rollback"))
				.to(SQL_ENDPOINT)
				.to("activemq:queue://message.out.2");

		from("activemq:queue://message.in.3")
				.id("transactionalRoute-3")
				.transacted()
				.to(SQL_ENDPOINT)
				.throwException(new IOException("Forced Exception 3. This should provoke rollback"))
				.to("activemq:queue://message.out.3");

		from("activemq:queue://message.in.4")
				.id("transactionalRoute-4")
				.transacted()
				.to(SQL_ENDPOINT)
				.to("activemq:queue://message.out.4")
				.throwException(new IOException("Forced Exception 4. This should provoke rollback"));
	}
}
