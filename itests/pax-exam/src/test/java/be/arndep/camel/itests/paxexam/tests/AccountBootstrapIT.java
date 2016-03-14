package be.arndep.camel.itests.paxexam.tests;

import be.arndep.camel.account.api.AccountCommandService;
import be.arndep.camel.account.api.AccountQueryService;
import be.arndep.camel.itests.paxexam.support.KarafSupportIT;
import org.apache.camel.CamelContext;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;

import javax.inject.Inject;
import java.util.List;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

/**
 * Created by arnaud.deprez on 2/01/16.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AccountBootstrapIT extends KarafSupportIT {
	@Inject
	@Filter("(camel.context.name=bankAccounts-rest)")
	protected CamelContext camelContext;

	@Configuration
	public Option[] config() {
		List<Option> config = karafBase();
		// this is the key... we can install features, bundles, etc. using these pax-exam options
		config.add(
			features(maven()
					.groupId("be.arndep.camel")
					.artifactId("features")
					.versionAsInProject()
					.classifier("features")
					.type("xml"),
				"account-rest")
		);
		return config.toArray(new Option[config.size()]);
	}

	@Test
	public void testInstallation() throws Exception {
		assertFeatureInstalled("account-rest", System.getProperty("project.version"));
		assertBundleInstalled("be.arndep.camel.account-api", Bundle.ACTIVE);
		assertBundleInstalled("be.arndep.camel.account-impl", Bundle.ACTIVE);
		assertBundleInstalled("be.arndep.camel.account-rest", Bundle.ACTIVE);
	}

	@Test
	public void testServiceInjection() throws Exception {
		Assertions.assertThat(camelContext).isNotNull();
		Assertions.assertThat(camelContext.getRegistry().lookupByNameAndType("accountCommandService", AccountCommandService.class)).isNotNull();
		Assertions.assertThat(camelContext.getRegistry().lookupByNameAndType("accountQueryService", AccountQueryService.class)).isNotNull();
	}
}
