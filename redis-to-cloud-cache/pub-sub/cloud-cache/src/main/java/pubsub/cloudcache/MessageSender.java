package pubsub.cloudcache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    @Cacheable("chat")
    public String sendMessage(String message) {
        return message;
    }
}
