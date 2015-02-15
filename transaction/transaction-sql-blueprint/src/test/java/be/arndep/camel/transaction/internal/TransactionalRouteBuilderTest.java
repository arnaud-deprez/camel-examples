package be.arndep.camel.transaction.internal;

import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

public class TransactionalRouteBuilderTest extends CamelBlueprintTestSupport {

	@EndpointInject(uri = "mock://activemq.DLQ")
	MockEndpoint dlq;

	private JdbcTemplate jdbcTemplate;
	private TransactionTemplate transactionTemplate;

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/transaction.xml," +
				"OSGI-INF/blueprint/activemq.xml," +
				"OSGI-INF/blueprint/datasource.xml," +
				"OSGI-INF/blueprint/blueprint.xml";
	}

	@Override
	public void setUp() throws Exception {
		
		super.setUp();
		
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("activemq:queue://ActiveMQ.DLQ")
						.log(LoggingLevel.WARN, "@@@ Receive a message in the DLQ ${body}")
						.to(dlq);
			}
		});

		DataSource dataSource = context.getRegistry().lookupByNameAndType("dataSource", DataSource.class);
		jdbcTemplate = new JdbcTemplate(dataSource);
		assertNotNull(jdbcTemplate);
		
		PlatformTransactionManager platformTransactionManager = context.getRegistry().lookupByNameAndType("transactionManager", PlatformTransactionManager.class);
		transactionTemplate = new TransactionTemplate(platformTransactionManager);
		assertNotNull(transactionTemplate);
		
		transactionTemplate.execute(s -> {
			jdbcTemplate.execute("CREATE TABLE message (id BIGINT auto_increment primary key, content varchar(255));");
			return s;
		});
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		transactionTemplate.execute(s -> {
			JdbcTestUtils.dropTables(jdbcTemplate, "message");
			return s;
		});
	}

	@Test
	public void testTransactionalRoute1() throws Exception {
		final String routeId = "transactionalRoute-1";
		assertNotNull(context.getRouteDefinition(routeId));

		final MockEndpoint outputQueue = getMockEndpoint("mock://message.out.1");
		context.getRouteDefinition(routeId).adviceWith(context, new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint(TransactionalRouteBuilder.SQL_ENDPOINT)
						.to(outputQueue);

				interceptSendToEndpoint("activemq:queue://message.out.1")
						.log("@@@ Receive message ${body}")
						.to(outputQueue);
			}
		});

		String body = "Hello World!";
		outputQueue.expectedMessageCount(1);
		outputQueue.expectedBodiesReceived(body);
		dlq.expectedMessageCount(0);

		template.sendBody("activemq:queue://message.in.1", body);

		assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
		Thread.sleep(2000);
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "message"));
	}

	@Test
	public void testTransactionalRoute2() throws Exception {
		final String routeId = "transactionalRoute-2";
		assertNotNull(context.getRouteDefinition(routeId));

		final MockEndpoint outputQueue = getMockEndpoint("mock://message.out.2");
		context.getRouteDefinition(routeId).adviceWith(context, new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("activemq:queue://message.out.2")
						.log("@@@ Receive message ${body}")
						.to(outputQueue);
			}
		});

		String body = "Hello World!";
		outputQueue.expectedMessageCount(0);
		dlq.expectedMessageCount(1);
		dlq.expectedBodiesReceived(body);

		template.sendBody("activemq:queue://message.in.2", body);

		assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "message"));
	}

	@Test
	public void testTransactionalRoute3() throws Exception {
		final String routeId = "transactionalRoute-3";
		assertNotNull(context.getRouteDefinition(routeId));

		final MockEndpoint outputQueue = getMockEndpoint("mock://message.out.3");
		context.getRouteDefinition(routeId).adviceWith(context, new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("activemq:queue://message.out.3")
						.log("@@@ Receive message ${body}")
						.to(outputQueue);
			}
		});

		String body = "Hello World!";
		outputQueue.expectedMessageCount(0);
		dlq.expectedMessageCount(1);
		dlq.expectedBodiesReceived(body);

		template.sendBody("activemq:queue://message.in.3", body);

		assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "message"));
	}

	@Test
	public void testTransactionalRoute4() throws Exception {
		final String routeId = "transactionalRoute-4";
		assertNotNull(context.getRouteDefinition(routeId));

		final MockEndpoint outputQueue = getMockEndpoint("mock://message.out.4");
		context.getRouteDefinition(routeId).adviceWith(context, new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("activemq:queue://message.out.4")
						.log("@@@ Receive message ${body}")
						.to(outputQueue);
			}
		});

		String body = "Hello World!";
		// It's not transactional when sending message !
		outputQueue.expectedMessageCount(1);
		outputQueue.expectedBodiesReceived(body);
		dlq.expectedMessageCount(1);
		dlq.expectedBodiesReceived(body);

		template.sendBody("activemq:queue://message.in.4", body);

		assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "message"));
	}
}