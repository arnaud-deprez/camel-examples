package be.arndep.camel.transaction.sql.internal.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;
import java.util.Date;

/**
 * Created by arnaud on 15/02/15.
 */
public class TransactionalRouteBuilder extends RouteBuilder {
	
	public static final String SQL_ENDPOINT = "sql://INSERT INTO MESSAGE (createddate, lastmodifieddate, content) VALUES (:#${header.createdDate}, :#${header.createdDate}, :#${body})";
	
	@Override
	public void configure() throws Exception {
		from("activemq:queue://xa.jms.sql.in.1")
				.id("transactionalRoute-1")
				.transacted()
				.log("@@ Receive message ${body}")
				.setHeader("createdDate", exchangeProperty(Exchange.CREATED_TIMESTAMP))
				.to(SQL_ENDPOINT)
				.to("activemq:queue://xa.jms.sql.out.1")
				.log("@@ Finish to process ${body}");

		from("activemq:queue://xa.jms.sql.in.2")
				.id("transactionalRoute-2")
				.transacted()
				.log("@@ Receive message ${body}")
				.throwException(new IOException("Forced Exception 2. This should provoke rollback"))
				.setHeader("createdDate", exchangeProperty(Exchange.CREATED_TIMESTAMP))
				.to(SQL_ENDPOINT)
				.to("activemq:queue://xa.jms.sql.out.2");

		from("activemq:queue://xa.jms.sql.in.3")
				.id("transactionalRoute-3")
				.transacted()
				.log("@@ Receive message ${body}")
				.setHeader("createdDate", exchangeProperty(Exchange.CREATED_TIMESTAMP))
				.to(SQL_ENDPOINT)
				.throwException(new IOException("Forced Exception 3. This should provoke rollback"))
				.to("activemq:queue://xa.jms.sql.out.3");

		from("activemq:queue://xa.jms.sql.in.4")
				.id("transactionalRoute-4")
				.transacted()
				.log("@@ Receive message ${body}")
				.setHeader("createdDate", exchangeProperty(Exchange.CREATED_TIMESTAMP))
				.to(SQL_ENDPOINT)
				.to("activemq:queue://xa.jms.sql.out.4")
				.throwException(new IOException("Forced Exception 4. This should provoke rollback"));
	}
}
