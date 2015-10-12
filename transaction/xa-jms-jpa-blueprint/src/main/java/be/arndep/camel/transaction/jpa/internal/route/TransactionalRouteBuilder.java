package be.arndep.camel.transaction.jpa.internal.route;

import be.arndep.camel.transaction.jpa.message.MessageFactory;
import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;

/**
 * Created by arnaud on 03.01.15.
 */
public class TransactionalRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //@formatter:off
        MessageFactory messageFactory = new MessageFactory();

        from("activemq:queue://xa.jms.jpa.in.1")
                .id("transactionalRoute-1")
                .transacted()
                .log("@@ Receive message ${body}")
                .transform().body(String.class).bean(messageFactory, "create")
				.to("bean:messageRepository?method=create(${body})")
				.transform().simple("${body.content}")
				.to("activemq:queue://xa.jms.jpa.out.1")
                .log("@@ Finish to process ${body}");

        from("activemq:queue://xa.jms.jpa.in.2")
                .id("transactionalRoute-2")
                .transacted()
                .log("@@ Receive message ${body}")
                .transform().body(String.class).bean(messageFactory, "create")
                .throwException(new IOException("Forced Exception 2. This should provoke rollback"))
				.to("bean:messageRepository?method=create(${body})")
				.transform().simple("${body.content}")
                .to("activemq:queue://xa.jms.jpa.out.2");

        from("activemq:queue://xa.jms.jpa.in.3")
                .id("transactionalRoute-3")
                .transacted()
                .log("@@ Receive message ${body}")
                .transform().body(String.class).bean(messageFactory, "create")
				.to("bean:messageRepository?method=create(${body})")
				.transform().simple("${body.content}")
                .throwException(new IOException("Forced Exception 3. This should provoke rollback"))
                .to("activemq:queue://xa.jms.jpa.out.3");

        from("activemq:queue://xa.jms.jpa.in.4")
                .id("transactionalRoute-4")
                .transacted()
                .log("@@ Receive message ${body}")
                .transform().body(String.class).bean(messageFactory, "create")
				.to("bean:messageRepository?method=create(${body})")
				.transform().simple("${body.content}")
                .to("activemq:queue://xa.jms.jpa.out.4")
                .throwException(new IOException("Forced Exception 4. This should provoke rollback"));
        //@formatter:on
    }
}
