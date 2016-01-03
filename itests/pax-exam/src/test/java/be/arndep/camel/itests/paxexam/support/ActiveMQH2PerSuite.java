package be.arndep.camel.itests.paxexam.support;

import org.apache.activemq.broker.BrokerService;
import org.h2.tools.Server;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.spi.StagedExamReactor;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactor;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by arnaud.deprez on 3/01/16.
 */
public class ActiveMQH2PerSuite extends PerSuite {
	private static final Logger LOG = LoggerFactory.getLogger(ActiveMQH2PerSuite.class);

	private Server server;
	private BrokerService broker;

	@Override
	public StagedExamReactor create(List<TestContainer> containers, List<TestProbeBuilder> mProbes) {
		return new EagerSingleStagedReactor(containers, mProbes) {

			@Override
			public void beforeSuite() {
				LOG.info(">>>>>> Setup before suite <<<<<<<<<");
				setUpBroker();
				setUpH2();
				super.beforeSuite();
			}

			@Override
			public void afterSuite() {
				LOG.info(">>>>>> Tear down after suite <<<<<<<<<");
				tearDownH2();
				tearDownBroker();
				super.afterSuite();
			}
		};
	}

	private void setUpBroker() {
		try {
			broker = new BrokerService();
			broker.addConnector("nio://0.0.0.0:" + System.getProperty(KarafSupportIT.ACTIVEMQ_TCP_PORT));
			broker.setPersistent(false);
			broker.start();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void tearDownBroker() {
		try {
			broker.stop();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setUpH2() {
		try {
			server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", System.getProperty(KarafSupportIT.H2_TCP_PORT), "-baseDir", "target/h2-data").start();
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void tearDownH2() {
		server.stop();
	}
}
