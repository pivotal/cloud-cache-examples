package io.pivotal.cloudcache.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration;

@SpringBootApplication
// Causes the creation of server-side PCC/GemFire regions via the @Cacheable annotation during the
// Initialization phase of the app's lifecycle
@EnableCachingDefinedRegions
public class HelloWorldApplication {

	@Configuration
	@Profile("!localCluster")
	// Allows Spring to configure the PCC/Gemfire cluster; necessary for creating regions
	@EnableClusterConfiguration(useHttp = true)
	static class CloudConfiguration {

	}

	@Configuration
	@Profile("localCluster")
	@EnableClusterConfiguration(useHttp = true, requireHttps = false)
	static class LocalConfiguration {

	}

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}
}
