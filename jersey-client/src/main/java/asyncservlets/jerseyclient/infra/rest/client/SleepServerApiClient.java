package asyncservlets.jerseyclient.infra.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;

public class SleepServerApiClient {
	private final Client client;
	private final WebTarget target;

	public SleepServerApiClient() throws Exception {
		ClientConfig conf = new ClientConfig();
		conf.connectorProvider(new GrizzlyConnectorProvider());
		conf.property(ClientProperties.ASYNC_THREADPOOL_SIZE, 100);
		client = ClientBuilder.newClient(conf);
		target = client.target("http://localhost:8001");
	}

	public void getSleepResource(long replyAfterMillis,
			final Callback<String> callback) {
		target.queryParam("replyAfterMillis", replyAfterMillis).request()
				.async().get(new InvocationCallback<String>() {
					@Override
					public void completed(String response) {
						callback.callback(response);
					}

					@Override
					public void failed(Throwable t) {
						t.printStackTrace();
					}
				});
	}

	public void close() {
		client.close();
	}

	public static interface Callback<T> {
		void callback(T result);
	}
}
