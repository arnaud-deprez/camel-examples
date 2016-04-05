package be.arndep.camel.itests.paxexam.support;

import org.apache.karaf.features.BootFinished;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;
import java.io.*;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.*;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created by arnaud.deprez on 8/12/15.
 */
public abstract class KarafSupportIT {
	static final Long COMMAND_TIMEOUT = 10000L;
	static final Long SERVICE_TIMEOUT = 30000L;
	static final Long BUNDLE_TIMEOUT = 30000L;

	protected static Logger LOG = LoggerFactory.getLogger(KarafSupportIT.class);

	// can further enhance this to auto discover the ports at run time
	// so they don't conflict with other tests/running versions of Fuse
	public static final String RMI_SERVER_PORT = "karaf.rmi.server.port";
	public static final String HTTP_PORT = "karaf.http.port";
	public static final String RMI_REG_PORT = "karaf.rmi.reg.port";
	public static final String H2_TCP_PORT = "h2.tcp.port";
	public static final String ACTIVEMQ_TCP_PORT = "activemq.tcp.port";

	@Rule
	public KarafTestWatcher baseTestWatcher = new KarafTestWatcher();

	ExecutorService executor = Executors.newCachedThreadPool();

	@Inject
	protected FeaturesService featureService;

	@Inject
	protected SessionFactory sessionFactory;

	/**
	 * To make sure the tests run only when the boot features are fully installed
	 */
	@Inject
	BootFinished bootFinished;

	@Inject
	protected BundleContext bundleContext;

	protected List<Option> karafBase() {
		LOG.info("karaf.http.port = {}", System.getProperty(HTTP_PORT));
		LOG.info("karaf.rmi.server.port = {}", System.getProperty(RMI_SERVER_PORT));
		LOG.info("karaf.rmi.reg.port = {}", System.getProperty(RMI_REG_PORT));
		LOG.info("h2.tcp.port = {}", System.getProperty(H2_TCP_PORT));
		LOG.info("activemq.tcp.port = {}", System.getProperty(ACTIVEMQ_TCP_PORT));

		List<Option> result = new ArrayList<>();

		MavenArtifactUrlReference karafUrl = maven()
			.groupId("org.apache.karaf")
			.artifactId("apache-karaf")
			.versionAsInProject()
			.type("tar.gz");

		result.add(
			karafDistributionConfiguration()
				.frameworkUrl(karafUrl)
				.useDeployFolder(false)
				.name("Karaf")
				.unpackDirectory(new File("target/paxexam/unpack"))
		);
		result.add(configureConsole().ignoreLocalConsole());
		result.add(logLevel(LogLevelOption.LogLevel.INFO));
//		result.add(editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.apache.org/content/repositories/orgapachecamel-1049@id=camel"));
		result.add(editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", System.getProperty(HTTP_PORT)));
		result.add(editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", System.getProperty(RMI_REG_PORT)));
		result.add(editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", System.getProperty(RMI_SERVER_PORT)));
		result.add(editConfigurationFilePut("etc/users.properties", "admin", "admin,admin"));
		//edit h2 datasource config
//		result.add(editConfigurationFilePut("etc/be.arndep.camel.h2.cfg", "db.url", "jdbc:h2:tcp://localhost:" + System.getProperty(H2_TCP_PORT) + "/./target/h2-data"));
		result.add(editConfigurationFilePut("etc/be.arndep.camel.h2.cfg", "db.url", "jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1"));
		//edit activemq config
		result.add(editConfigurationFilePut("etc/be.arndep.activemq.pool.cfg", "broker.url", "vm://localhost?broker.persistent=false&broker.useJmx=false"));
//		result.add(editConfigurationFilePut("etc/be.arndep.activemq.pool.cfg", "broker.url", "failover:(nio://localhost:" + System.getProperty(ACTIVEMQ_TCP_PORT) + ")"));

		result.add(junitBundles());
		result.add(
			mavenBundle()
				.groupId("org.assertj")
				.artifactId("assertj-core")
				.versionAsInProject()
		);

		return result;
	}

	/**
	 * Executes a shell command and returns output as a String.
	 * Commands have a default timeout of 10 seconds.
	 *
	 * @param command    The command to execute
	 * @param principals The principals (e.g. RolePrincipal objects) to run the command under
	 * @return
	 */
	protected String executeCommand(final String command, Principal... principals) {
		return executeCommand(command, COMMAND_TIMEOUT, false, principals);
	}

	/**
	 * Executes a shell command and returns output as a String.
	 * Commands have a default timeout of 10 seconds.
	 *
	 * @param command    The command to execute.
	 * @param timeout    The amount of time in millis to wait for the command to execute.
	 * @param silent     Specifies if the command should be displayed in the screen.
	 * @param principals The principals (e.g. RolePrincipal objects) to run the command under
	 * @return
	 */
	protected String executeCommand(final String command, final Long timeout, final Boolean silent, final Principal... principals) {
		waitForCommandService(command);

		String response;
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final PrintStream printStream = new PrintStream(byteArrayOutputStream);
		final SessionFactory sessionFactory = getOsgiService(SessionFactory.class);
		final Session session = sessionFactory.create(System.in, printStream, System.err);

		final Callable<String> commandCallable = () -> {
			try {
				if (!silent) {
					System.err.println(command);
				}
				session.execute(command);
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			printStream.flush();
			return byteArrayOutputStream.toString();
		};

		FutureTask<String> commandFuture;
		if (principals.length == 0) {
			commandFuture = new FutureTask<>(commandCallable);
		}
		else {
			// If principals are defined, run the command callable via Subject.doAs()
			commandFuture = new FutureTask<>(() -> {
				Subject subject = new Subject();
				subject.getPrincipals().addAll(Arrays.asList(principals));
				return Subject.doAs(subject, (PrivilegedExceptionAction<String>) () -> commandCallable.call());
			});
		}

		try {
			executor.submit(commandFuture);
			response = commandFuture.get(timeout, TimeUnit.MILLISECONDS);
		}
		catch (TimeoutException e) {
			e.printStackTrace(System.err);
			response = "SHELL COMMAND TIMED OUT: ";
		}
		catch (ExecutionException e) {
			Throwable cause = e.getCause().getCause();
			throw new RuntimeException(cause.getMessage(), cause);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return response;
	}


	protected <T> T getOsgiService(Class<T> type, long timeout) {
		return getOsgiService(type, null, timeout);
	}

	protected <T> T getOsgiService(Class<T> type) {
		return getOsgiService(type, null, SERVICE_TIMEOUT);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
		ServiceTracker tracker = null;
		try {
			String flt;
			if (filter != null) {
				if (filter.startsWith("(")) {
					flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")" + filter + ")";
				}
				else {
					flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")(" + filter + "))";
				}
			}
			else {
				flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
			}
			Filter osgiFilter = FrameworkUtil.createFilter(flt);
			tracker = new ServiceTracker(bundleContext, osgiFilter, null);
			tracker.open(true);
			// Note that the tracker is not closed to keep the reference
			// This is buggy, as the service reference may change i think
			Object svc = type.cast(tracker.waitForService(timeout));
			if (svc == null) {
				Dictionary dic = bundleContext.getBundle().getHeaders();
				System.err.println("Test bundle headers: " + explode(dic));

				for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, null))) {
					System.err.println("ServiceReference: " + ref);
				}

				for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, flt))) {
					System.err.println("Filtered ServiceReference: " + ref);
				}

				throw new RuntimeException("Gave up waiting for service " + flt);
			}
			return type.cast(svc);
		}
		catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("Invalid filter", e);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void waitForCommandService(String command) {
		// the commands are represented by services. Due to the asynchronous nature of services they may not be
		// immediately available. This code waits the services to be available, in their secured form. It
		// means that the code waits for the command service to appear with the roles defined.

		if (command == null || command.length() == 0) {
			return;
		}

		int spaceIdx = command.indexOf(' ');
		if (spaceIdx > 0) {
			command = command.substring(0, spaceIdx);
		}
		int colonIndx = command.indexOf(':');
		String scope = (colonIndx > 0) ? command.substring(0, colonIndx) : "*";
		String name = (colonIndx > 0) ? command.substring(colonIndx + 1) : command;
		try {
			long start = System.currentTimeMillis();
			long cur = start;
			while (cur - start < SERVICE_TIMEOUT) {
				if (sessionFactory.getRegistry().getCommand(scope, name) != null) {
					return;
				}
				Thread.sleep(100);
				cur = System.currentTimeMillis();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void waitForService(String filter, long timeout) throws InvalidSyntaxException, InterruptedException {
		ServiceTracker<Object, Object> st = new ServiceTracker<>(bundleContext, bundleContext.createFilter(filter), null);
		try {
			st.open();
			st.waitForService(timeout);
		}
		finally {
			st.close();
		}
	}

	protected Bundle waitBundleState(String symbolicName, int state) {
		long endTime = System.currentTimeMillis() + BUNDLE_TIMEOUT;
		while (System.currentTimeMillis() < endTime) {
			Bundle bundle = findBundleByName(symbolicName);
			if (bundle != null && bundle.getState() == state) {
				return bundle;
			}
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
		Assert.fail("Manadatory bundle " + symbolicName + " not found.");
		throw new IllegalStateException("Should not be reached");
	}

	/*
	* Explode the dictionary into a ,-delimited list of key=value pairs
	*/
	@SuppressWarnings("rawtypes")
	private static String explode(Dictionary dictionary) {
		Enumeration keys = dictionary.keys();
		StringBuffer result = new StringBuffer();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			result.append(String.format("%s=%s", key, dictionary.get(key)));
			if (keys.hasMoreElements()) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	/**
	 * Provides an iterable collection of references, even if the original array is null
	 */
	@SuppressWarnings("rawtypes")
	private static Collection<ServiceReference> asCollection(ServiceReference[] references) {
		return references != null ? Arrays.asList(references) : Collections.emptyList();
	}

	public JMXConnector getJMXConnector() throws Exception {
		return getJMXConnector("karaf", "karaf");
	}

	public JMXConnector getJMXConnector(String userName, String passWord) throws Exception {
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + RMI_REG_PORT + "/karaf-root");
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		String[] credentials = new String[]{userName, passWord};
		env.put("jmx.remote.credentials", credentials);
		JMXConnector connector = JMXConnectorFactory.connect(url, env);
		return connector;
	}

	public void assertFeatureInstalled(String featureName) throws Exception {
		String name;
		String version;
		if (featureName.contains("/")) {
			name = featureName.substring(0, featureName.indexOf("/"));
			version = featureName.substring(featureName.indexOf("/") + 1);
		}
		else {
			name = featureName;
			version = null;
		}
		assertFeatureInstalled(name, version);
	}

	public void assertFeatureInstalled(String featureName, String featureVersion) throws Exception {
		Feature featureToAssert = featureService.getFeatures(featureName, featureVersion)[0];
		Feature[] features = featureService.listInstalledFeatures();
		for (Feature feature : features) {
			if (featureToAssert.equals(feature)) {
				return;
			}
		}
		Assert.fail("Feature " + featureName + (featureVersion != null ? "/" + featureVersion : "") + " should be installed but is not");
	}

	public void assertFeaturesInstalled(String... expectedFeatures) throws Exception {
		Set<String> expectedFeaturesSet = new HashSet<>(Arrays.asList(expectedFeatures));
		Feature[] features = featureService.listInstalledFeatures();
		Set<String> installedFeatures = new HashSet<>();
		for (Feature feature : features) {
			installedFeatures.add(feature.getName());
		}
		String msg = "Expecting the following features to be installed : " + expectedFeaturesSet + " but found " + installedFeatures;
		Assert.assertTrue(msg, installedFeatures.containsAll(expectedFeaturesSet));
	}

	public void assertFeatureNotInstalled(String featureName) throws Exception {
		String name;
		String version;
		if (featureName.contains("/")) {
			name = featureName.substring(0, featureName.indexOf("/"));
			version = featureName.substring(featureName.indexOf("/") + 1);
		}
		else {
			name = featureName;
			version = null;
		}
		assertFeatureNotInstalled(name, version);
	}

	public void assertFeatureNotInstalled(String featureName, String featureVersion) throws Exception {
		Feature featureToAssert = featureService.getFeatures(featureName, featureVersion)[0];
		Feature[] features = featureService.listInstalledFeatures();
		for (Feature feature : features) {
			if (featureToAssert.equals(feature)) {
				Assert.fail("Feature " + featureName + (featureVersion != null ? "/" + featureVersion : "") + " is installed whereas it should not be");
			}
		}
	}

	public void assertBundleInstalled(String name) {
		Assert.assertNotNull("Bundle " + name + " should be installed", findBundleByName(name));
	}

	public void assertBundleInstalled(String name, int state) {
		waitBundleState(name, state);
	}

	public void assertBundleNotInstalled(String name) {
		Assert.assertNull("Bundle " + name + " should not be installed", findBundleByName(name));
	}

	protected Bundle findBundleByName(String symbolicName) {
		for (Bundle bundle : bundleContext.getBundles()) {
			if (bundle.getSymbolicName().equals(symbolicName)) {
				return bundle;
			}
		}
		return null;
	}

	protected void installAndAssertFeature(String feature) throws Exception {
		featureService.installFeature(feature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
		assertFeatureInstalled(feature);
	}

	protected void installAssertAndUninstallFeature(String feature, String version) throws Exception {
		installAssertAndUninstallFeatures(feature + "/" + version);
	}

	protected void installAssertAndUninstallFeatures(String... feature) throws Exception {
		boolean success = false;
		try {
			for (String curFeature : feature) {
				featureService.installFeature(curFeature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
				assertFeatureInstalled(curFeature);
			}
			success = true;
		}
		finally {
			for (String curFeature : feature) {
				System.out.println("Uninstalling " + curFeature);
				try {
					featureService.uninstallFeature(curFeature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
				}
				catch (Exception e) {
					if (success) {
						throw e;
					}
				}
			}
		}
	}

	/**
	 * The feature service does not uninstall feature dependencies when uninstalling a single feature.
	 * So we need to make sure we uninstall all features that were newly installed.
	 *
	 * @param featuresBefore
	 * @throws Exception
	 */
	protected void uninstallNewFeatures(Set<Feature> featuresBefore)
		throws Exception {
		Feature[] features = featureService.listInstalledFeatures();
		for (Feature curFeature : features) {
			if (!featuresBefore.contains(curFeature)) {
				try {
					System.out.println("Uninstalling " + curFeature.getName());
					featureService.uninstallFeature(curFeature.getName(), curFeature.getVersion(),
						EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
				}
				catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	protected void close(Closeable closeAble) {
		if (closeAble != null) {
			try {
				closeAble.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
}
