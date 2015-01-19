package be.arndep.camel.rest.config;

import org.apache.camel.component.swagger.spring.SpringRestSwaggerApiDeclarationServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arnaud on 01.01.15.
 */
@Configuration
public class SwaggerConfiguration {
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
