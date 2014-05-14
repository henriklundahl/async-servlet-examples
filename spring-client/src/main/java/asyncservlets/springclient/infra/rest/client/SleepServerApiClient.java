package asyncservlets.springclient.infra.rest.client;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

public class SleepServerApiClient {
	private final PoolingNHttpClientConnectionManager connectionManager;
	private final AsyncRestTemplate client;

	public SleepServerApiClient() throws Exception {
		connectionManager = new PoolingNHttpClientConnectionManager(
				new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
		connectionManager.setMaxTotal(20000);
		connectionManager.setDefaultMaxPerRoute(20000);

		RequestConfig config = RequestConfig.custom().setConnectTimeout(120000)
				.build();

		CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder.create()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(config).build();

		HttpComponentsAsyncClientHttpRequestFactory requestFactory = new HttpComponentsAsyncClientHttpRequestFactory(
				httpClient);
		client = new AsyncRestTemplate(requestFactory);
	}

	public void getSleepResource(long replyAfterMillis,
			final Callback<String> callback) throws Exception {
		ListenableFuture<ResponseEntity<String>> response = client
				.getForEntity(
						"http://localhost:8001?replyAfterMillis={replyAfterMillis}",
						String.class, replyAfterMillis);
		response.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
			@Override
			public void onSuccess(ResponseEntity<String> result) {
				callback.callback(result.getBody());
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	public void close() throws Exception {
		connectionManager.shutdown();
	}

	public static interface Callback<T> {
		void callback(T result);
	}
}
