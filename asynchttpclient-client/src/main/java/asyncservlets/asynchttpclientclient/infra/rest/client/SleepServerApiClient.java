package asyncservlets.asynchttpclientclient.infra.rest.client;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

public class SleepServerApiClient {
	private final AsyncHttpClient client;
	private final BoundRequestBuilder requestBuilder;

	public SleepServerApiClient() {
		AsyncHttpClientConfig conf = new AsyncHttpClientConfig.Builder()
				.setIOThreadMultiplier(1).setRequestTimeoutInMs(120000).build();
		client = new AsyncHttpClient(conf);
		requestBuilder = client.prepareGet("http://localhost:8001");
	}

	public void getSleepResource(long replyAfterMillis,
			final Callback<String> callback) throws Exception {
		requestBuilder.addQueryParameter("replyAfterMillis",
				Long.toString(replyAfterMillis)).execute(
				new AsyncCompletionHandler<Response>() {
					@Override
					public Response onCompleted(Response response)
							throws Exception {
						callback.callback(response.getResponseBody());
						return response;
					}

					@Override
					public void onThrowable(Throwable t) {
						t.printStackTrace();
					}
				});
	}

	public void close() {
		client.closeAsynchronously();
	}

	public static interface Callback<T> {
		void callback(T result);
	}
}
