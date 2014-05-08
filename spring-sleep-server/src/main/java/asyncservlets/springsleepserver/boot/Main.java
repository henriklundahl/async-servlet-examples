package asyncservlets.springsleepserver.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("asyncservlets.springsleepserver.infra.rest.api")
@EnableAutoConfiguration
public class Main {
	public static void main(String[] args) throws Exception {
		System.setProperty("server.port", "8001");
		System.setProperty("server.tomcat.max-threads", "1");
		SpringApplication.run(Main.class, args);
	}
}
