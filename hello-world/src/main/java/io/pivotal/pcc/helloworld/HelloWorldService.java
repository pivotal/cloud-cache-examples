package io.pivotal.pcc.helloworld;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    // Creates Region with name "Hello"
    @Cacheable("Hello")
    public String getHelloValue(String ignoredArgument) {
        simulateSlowDataStore();
        return "Initially called at " + System.nanoTime();
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
