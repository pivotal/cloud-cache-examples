package pubsub.redis;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(connectionFactory);
        redisMessageListenerContainer.addMessageListener(listenerAdapter, new PatternTopic("chat"));

        return redisMessageListenerContainer;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Subscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "receiveMessage");
    }

    @Bean
    Subscriber subscriber(CountDownLatch countDownLatch) {
        return new Subscriber(countDownLatch);
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    CountDownLatch countDownLatch() {
        return new CountDownLatch(1);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        CountDownLatch countDownLatch = applicationContext.getBean(CountDownLatch.class);

        LOGGER.info("Sending message...");
        stringRedisTemplate.convertAndSend("chat", "Hello from Redis!");

        countDownLatch.await();
    }
}
