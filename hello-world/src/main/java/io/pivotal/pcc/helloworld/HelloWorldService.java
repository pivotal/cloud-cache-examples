package io.pivotal.pcc.helloworld;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

@Service
public class HelloWorldService {
    // Creates Region with name "Hello"

    @Cacheable("Hello")
    public String getHelloValue(String ignoredArgument) {
        simulateSlowDataStore();

        Instant instant  = Instant.ofEpochMilli(System.currentTimeMillis());
        LocalDateTime localDateTime =  LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return  localDateTime.toString();
    }

    private void simulateSlowDataStore() {
        try {
            long artificialDelay = 3000L;
            Thread.sleep(artificialDelay);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
