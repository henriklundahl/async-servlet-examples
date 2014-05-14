package asyncservlets.forwardservlet.infra.rest.api;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import asyncservlets.asynchttpclientclient.infra.rest.client.SleepServerApiClient;
import asyncservlets.asynchttpclientclient.infra.rest.client.SleepServerApiClient.Callback;

public class RootResourceServlet extends HttpServlet {
	private static AtomicInteger pendingRequests = new AtomicInteger();

	private final SleepServerApiClient client;

	public RootResourceServlet(SleepServerApiClient client) {
		this.client = client;
	}

	@Override
	protected void doGet(final HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int requests = requestsParam(req);
		long replyAfterMillis = replyAfterMillisParam(req);
		System.out.println(pendingRequests.incrementAndGet());
		final AsyncContext context = req.startAsync();
		context.setTimeout(120000);
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
					ServletResponse response = context.getResponse();
					response.setContentType("text/plain");
					byte[] entity = ("Replying after " + time + " milliseconds.\n")
							.getBytes();
					response.setContentLength(entity.length);
					try {
						response.getOutputStream().write(entity);
					} catch (IOException e) {
						e.printStackTrace();
					}
					context.complete();
				}
			});
	}

	private int requestsParam(HttpServletRequest req) {
		String requestsParam = req.getParameter("requests");
		if (requestsParam == null)
			requestsParam = "1";
		int requests = Integer.parseInt(requestsParam);
		if (requests <= 0)
			requests = 1;
		return requests;
	}

	private long replyAfterMillisParam(HttpServletRequest req) {
		String replyAfterMillisParam = req.getParameter("replyAfterMillis");
		if (replyAfterMillisParam == null)
			replyAfterMillisParam = "1000";
		long replyAfterMillis = Long.parseLong(replyAfterMillisParam);
		if (replyAfterMillis <= 0)
			replyAfterMillis = 1000;
		return replyAfterMillis;
	}
}
