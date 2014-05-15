package asyncservlets.sleepserver.infra.rest.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RootResourceServlet extends HttpServlet {
	private static AtomicInteger pendingRequests = new AtomicInteger();

	private Timer timer;

	public RootResourceServlet() {
		timer = new Timer();
	}

	@Override
	protected void doGet(final HttpServletRequest req, HttpServletResponse resp) {
		long replyAfterMillis = replyAfterMillisParam(req);
		System.out.println(pendingRequests.incrementAndGet());
		final AsyncContext context = req.startAsync();
		context.setTimeout(120000);
		final long before = System.currentTimeMillis();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(pendingRequests.decrementAndGet());
				long time = System.currentTimeMillis() - before;
				ServletResponse response = context.getResponse();
				response.setContentType("text/plain");
				response.setCharacterEncoding("UTF-8");
				byte[] entity = ("Replying after " + time + " milliseconds.\n")
						.getBytes(Charset.forName("UTF-8"));
				response.setContentLength(entity.length);
				try {
					response.getOutputStream().write(entity);
				} catch (IOException e) {
					e.printStackTrace();
				}
				context.complete();
			}
		}, replyAfterMillis);
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
