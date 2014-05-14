package asyncservlets.asynchttpclientclient.infra.rest.client;

import java.io.IOException;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

public class SleepServerApiClient {
	private final AsyncHttpClient client;
	private final String url;

	public SleepServerApiClient() {
		AsyncHttpClientConfig conf = new AsyncHttpClientConfig.Builder()
				.setIOThreadMultiplier(1).setRequestTimeoutInMs(120000).build();
		client = new AsyncHttpClient(conf);
		url = "http://localhost:8001";
	}

	public void getSleepResource(long replyAfterMillis,
			final Callback<String> callback) throws IOException {
		client.prepareGet(url)
				.addQueryParameter("replyAfterMillis",
						Long.toString(replyAfterMillis))
				.execute(new AsyncCompletionHandler<Response>() {
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
