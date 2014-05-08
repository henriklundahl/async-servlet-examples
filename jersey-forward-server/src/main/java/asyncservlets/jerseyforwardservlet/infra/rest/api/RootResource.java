package asyncservlets.jerseyforwardservlet.infra.rest.api;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import asyncservlets.jerseyclient.infra.rest.client.SleepServerApiClient;
import asyncservlets.jerseyclient.infra.rest.client.SleepServerApiClient.Callback;

@Path("")
public class RootResource {
	private static AtomicInteger pendingRequests = new AtomicInteger();

	private final SleepServerApiClient client;

	public RootResource(SleepServerApiClient client) {
		this.client = client;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public void root(@QueryParam("requests") int requests,
			@QueryParam("replyAfterMillis") long replyAfterMillis,
			@Suspended final AsyncResponse response) throws Exception {
		System.out.println(pendingRequests.incrementAndGet());
		if (requests <= 0)
			requests = 1;
		final AtomicInteger responsesLeft = new AtomicInteger(requests);
		final long before = System.currentTimeMillis();
		for (int i = 0; i < requests; i++)
			client.getSleepResource(replyAfterMillis, new Callback<String>() {
				@Override
				public void callback(String result) {
					if (responsesLeft.decrementAndGet() > 0)
						return;

					System.out.println(pendingRequests.decrementAndGet());
					long time = System.currentTimeMillis() - before;
					response.resume("Replying after " + time
							+ " milliseconds.\n");
				}
			});
	}
}
