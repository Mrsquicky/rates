package com.sedisys.rates.config;

import com.sedisys.rates.api.RateApiImpl;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		register(RateApiImpl.class);
		register(JacksonFeature.class);
		register(ObjectMapperContextResolver.class);
		register(ApiListingResource.class);
		register(SwaggerSerializers.class);

		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[]{"http"});
		beanConfig.setHost("localhost:8080");
		beanConfig.setBasePath("/api");
		beanConfig.setResourcePackage("com.sedisys.rates.api");
		beanConfig.setScan(true);

		property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
	}
}