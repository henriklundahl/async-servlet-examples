package asyncservlets.jerseyforwardservlet.boot;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import asyncservlets.jerseyclient.infra.rest.client.SleepServerApiClient;
import asyncservlets.jerseyforwardservlet.infra.rest.api.RootResource;

public class Main {
	public static void main(String[] args) throws Exception {
		Server s = new Server(new QueuedThreadPool(2, 1));

		s.setConnectors(new Connector[] { createConnector(s) });

		ServletContextHandler context = new ServletContextHandler(SESSIONS);
		context.setContextPath("/");
		ServletHolder servletHolder = new ServletHolder(createJerseyServlet());
		servletHolder.setInitOrder(1);
		context.addServlet(servletHolder, "/*");
		s.setHandler(context);

		s.start();
	}

	private static ServerConnector createConnector(Server s) {
		ServerConnector connector = new ServerConnector(s, 0, 1);
		connector.setHost("localhost");
		connector.setPort(8002);
		return connector;
	}

	private static ServletContainer createJerseyServlet() throws Exception {
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(new RootResource(new SleepServerApiClient()));
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		return servletContainer;
	}
}
