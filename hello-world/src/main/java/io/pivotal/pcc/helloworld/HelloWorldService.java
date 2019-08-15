package io.pivotal.pcc.helloworld;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

@Service
public class HelloWorldService {

    // @Cacheable creates Region with name "Hello", automatically uses argument of getHelloValue() as key
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
