package asyncservlets.forwardservlet.boot;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import asyncservlets.asynchttpclientclient.infra.rest.client.SleepServerApiClient;
import asyncservlets.forwardservlet.infra.rest.api.RootResourceServlet;

public class Main {
	public static void main(String[] args) throws Exception {
		Server s = new Server(new QueuedThreadPool(2, 1));

		s.setConnectors(new Connector[] { createConnector(s) });

		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		ServletHolder servletHolder = new ServletHolder(
				new RootResourceServlet(new SleepServerApiClient()));
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
}
