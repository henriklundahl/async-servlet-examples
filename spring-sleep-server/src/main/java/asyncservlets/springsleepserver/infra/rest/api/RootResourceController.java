package asyncservlets.springsleepserver.infra.rest.api;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
public class RootResourceController {
	private static AtomicInteger pendingRequests = new AtomicInteger();

	private Timer timer;

	public RootResourceController() {
		timer = new Timer();
	}

	@RequestMapping(value = "", method = GET, produces = "text/plain")
	@ResponseBody
	public DeferredResult<String> get(
			@RequestParam(value = "replyAfterMillis", required = false, defaultValue = "1000") long replyAfterMillis) {
		System.out.println(pendingRequests.incrementAndGet());
		final long before = System.currentTimeMillis();
		if (replyAfterMillis <= 0)
			replyAfterMillis = 1000;
		final DeferredResult<String> result = new DeferredResult<>(120000);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(pendingRequests.decrementAndGet());
				long time = System.currentTimeMillis() - before;
				result.setResult("Replying after " + time + " milliseconds.\n");
			}
		}, replyAfterMillis);
		return result;
	}
}
