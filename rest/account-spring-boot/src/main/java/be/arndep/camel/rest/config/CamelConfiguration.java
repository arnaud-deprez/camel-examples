package be.arndep.camel.rest.config;

import be.arndep.camel.rest.route.RestRouteBuilder;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.component.swagger.spring.SpringRestSwaggerApiDeclarationServlet;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
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
     * Camel context configuration
     * @param applicationContext
     * @return
     * @throws Exception
     */
    @Bean
    public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {
        SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
        camelContext.setName("accounts-rest");
        camelContext.setUseMDCLogging(true);
        camelContext.setUseBreadcrumb(true);
        camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
        camelContext.addRoutes(router());
        return camelContext;
    }
    
    public RoutesBuilder router() {
        return new RestRouteBuilder();
    }

    /**
     * Swagger Camel Configuration
     */
    @Bean
    public ServletRegistrationBean swaggerServlet() {
        ServletRegistrationBean swagger = new ServletRegistrationBean(new SpringRestSwaggerApiDeclarationServlet(), "/camel/api/api-docs/*");
        Map<String, String> params = new HashMap<>();
        params.put("base.path", "http://localhost:8080" + CamelConfiguration.CAMEL_URL_MAPPING.substring(0, CamelConfiguration.CAMEL_URL_MAPPING.length() - 2));
        params.put("api.title", "Camel REST Api Title");
        params.put("api.description", "Camel REST Api description");
        params.put("api.termsOfServiceUrl", "http://termsofservice.org");
        params.put("api.license", "LICENSE");
        params.put("api.licenseUrl", "License URL");
        swagger.setInitParameters(params);
        return swagger;
    }
}
