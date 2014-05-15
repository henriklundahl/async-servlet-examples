package asyncservlets.jerseysleepserver.infra.rest.api;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("")
public class RootResource {
	private static AtomicInteger pendingRequests = new AtomicInteger();

	private Timer timer;

	public RootResource() {
		timer = new Timer();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public void root(@QueryParam("replyAfterMillis") long replyAfterMillis,
			@Suspended final AsyncResponse response) {
		System.out.println(pendingRequests.incrementAndGet());
		final long before = System.currentTimeMillis();
		if (replyAfterMillis <= 0)
			replyAfterMillis = 1000;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(pendingRequests.decrementAndGet());
				long time = System.currentTimeMillis() - before;
				response.resume("Replying after " + time + " milliseconds.\n");
			}
		}, replyAfterMillis);
	}
}
