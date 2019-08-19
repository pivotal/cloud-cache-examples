package io.pivotal.pcc.helloworld;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class HelloWorldService {

    // @Cacheable automatically uses argument of getHelloValue() as key, caches returned value
    // (Region with name "Hello" will be created via @EnableCachingDefinedRegions on Application)
    @Cacheable("Hello")
    // This code will not be called if key/value pair found in cache
    public String getHelloValue(String key) {
        simulateSlowDataStore();

        String value = getTimeOfInitialLookup();
        return value;
    }

    private void simulateSlowDataStore() {
        try {
            long artificialDelay = 3000L;
            Thread.sleep(artificialDelay);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getTimeOfInitialLookup() {
        Instant instant =
                Instant.ofEpochMilli(System.currentTimeMillis());

        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return localDateTime.toString();
    }
}
