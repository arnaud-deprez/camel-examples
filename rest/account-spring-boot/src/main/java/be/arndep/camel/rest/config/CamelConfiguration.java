package be.arndep.camel.rest.config;

import be.arndep.camel.account.rest.BankAccountRestServiceRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.spi.RoutePolicyFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.swagger.servlet.RestSwaggerServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arnaud on 31.12.14.
 */
@Configuration
public class CamelConfiguration extends SpringBootServletInitializer  {

    public static final String CAMEL_URL_MAPPING = "/camel/api/*";
    private static final String CAMEL_SERVLET_NAME = "CamelServlet";

	/**
	 * Camel context configuration
	 * @return
	 */
	@Bean
	public CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				camelContext.setUseMDCLogging(true);
				camelContext.setUseBreadcrumb(true);
            /*<restConfiguration bindingMode="json" component="{{camel.rest.component}}" port="{{camel.rest.port}}" contextPath="{{camel.rest.contextPath}}">
            <dataFormatProperty key="prettyPrint" value="{{camel.dataFormat.prettyPrint}}"/>
            </restConfiguration>*/
				camelContext.getRestConfiguration().setBindingMode(RestConfiguration.RestBindingMode.json);
				camelContext.getRestConfiguration().setComponent("servlet");
			}
		};
	}

    /**
     * Servlet configuration for camel REST 
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), CAMEL_URL_MAPPING);
        registration.setName(CAMEL_SERVLET_NAME);
        registration.setLoadOnStartup(1);
        return registration;
    }

	/**
	 * Metrics for camel
	 * @return
	 */
	@Bean
	public RoutePolicyFactory metricRoutePolicyFactory() {
		return new MetricsRoutePolicyFactory();
	}

	/**
	 * My router
	 * @return
	 */
    @Bean
    public RoutesBuilder router() {
        return new BankAccountRestServiceRoute();
    }

    /**
     * Swagger Camel Configuration
     */
    @Bean
    public ServletRegistrationBean swaggerServlet() {
        ServletRegistrationBean swagger = new ServletRegistrationBean(new RestSwaggerServlet(), "/camel/api/api-docs/*");
        Map<String, String> params = new HashMap<>();
        params.put("base.path", "http://localhost:8080" + CamelConfiguration.CAMEL_URL_MAPPING.substring(0, CamelConfiguration.CAMEL_URL_MAPPING.length() - 2));
        params.put("api.title", "Camel REST Api Title");
        params.put("api.description", "Camel REST Api description");
        params.put("api.termsOfServiceUrl", "http://termsofservice.org");
        params.put("api.license", "LICENSE");
        params.put("api.licenseUrl", "License URL");
        params.put("cors", "true");
        swagger.setInitParameters(params);
        return swagger;
    }
}
