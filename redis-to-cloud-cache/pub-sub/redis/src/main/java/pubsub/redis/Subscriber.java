package pubsub.redis;

import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class Subscriber {
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

    private CountDownLatch countDownLatch;

    @Autowired
    public Subscriber(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void receiveMessage(String message) {
        LOGGER.info("Subscriber received <" + message + ">");
        countDownLatch.countDown();
    }
}