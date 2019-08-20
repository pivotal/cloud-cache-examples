package io.pivotal.pcc.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration;

@SpringBootApplication
@EnableCachingDefinedRegions // Creates PCC/Gemfire Regions via the @Cacheable annotation in HelloWorldService
// Causes the creation of server-side PCC/GemFire regions via the @Cacheable annotation during the
// initialization phase of the app's lifecycle
@EnableClusterConfiguration(useHttp = true) // Allows Spring to configure the PCC/Gemfire cluster; necessary for creating regions
public class HelloWorldApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}
}
