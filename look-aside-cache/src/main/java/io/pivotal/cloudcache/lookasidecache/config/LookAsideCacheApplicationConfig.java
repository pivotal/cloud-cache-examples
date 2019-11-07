package io.pivotal.cloudcache.lookasidecache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.geode.config.annotation.EnableClusterAware;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableCachingDefinedRegions
@EnableClusterAware
public class LookAsideCacheApplicationConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
