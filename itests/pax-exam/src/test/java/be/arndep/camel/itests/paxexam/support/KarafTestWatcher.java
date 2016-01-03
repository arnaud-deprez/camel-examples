package be.arndep.camel.itests.paxexam.support;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by arnaud.deprez on 8/12/15.
 */
public class KarafTestWatcher extends TestWatcher {
	private static final Logger LOG = LoggerFactory.getLogger(KarafTestWatcher.class);

	@Override
	protected void starting(Description description) {
		LOG.info(">>>>>> Starts {} <<<<<" , description.getDisplayName());
	}

	@Override
	protected void failed(Throwable e, Description description) {
		LOG.error(">>>>>> FAILED: {} , cause: {}", description.getDisplayName(), e.getMessage());
	}

	@Override
	protected void succeeded(Description description) {
		LOG.info(">>>>>> {} Succeeded! <<<<<" , description.getDisplayName());
	}
}
