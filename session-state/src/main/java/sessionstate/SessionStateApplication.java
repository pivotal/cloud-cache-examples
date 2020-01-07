package sessionstate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.geode.config.annotation.EnableClusterAware;

@SpringBootApplication
@EnableClusterAware
public class SessionStateApplication {
	public static void main(String[] args) {
		SpringApplication.run(SessionStateApplication.class, args);
	}
}
