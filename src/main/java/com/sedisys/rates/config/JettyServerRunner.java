package com.sedisys.rates.config;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.servlets.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class JettyServerRunner {

	public static void main(String[] args) throws Exception {
		ServletContextHandler context = new ServletContextHandler();

		context.setContextPath("/");

		JerseyConfig jerseyConfig = new JerseyConfig();
		ServletHolder servletHolder = new ServletHolder(new ServletContainer(jerseyConfig));

		context.addServlet(servletHolder, "/rest/*");

		MetricRegistry metricRegistry = new MetricRegistry();

		servletHolder = new ServletHolder(new MetricsServlet(metricRegistry));
		context.addServlet(servletHolder, "/metrics");

		context.addEventListener(new ContextLoaderListener());
		context.addEventListener(new RequestContextListener());

		context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
		context.setInitParameter("contextConfigLocation", SpringConfig.class.getName());
		if (args.length==1){
			context.setInitParameter("jsonFilePath", args[0]);
		}

		InstrumentedHandler instrumentedHandler = new InstrumentedHandler(metricRegistry);
		instrumentedHandler.setHandler(context);

		Server server = new Server(8080);
		server.setHandler(instrumentedHandler);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}