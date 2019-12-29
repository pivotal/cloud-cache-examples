<!--Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.

This program and the accompanying materials are made available under the terms of the under the Apache License, Version
2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.-->

# Publish And Subscribe

The projects in this directory illustrate a Spring Boot application that creates a publish and subscribe channel with
either Redis or Cloud Cache. In this guide, we will highlight the changes necessary for switching from Redis to Cloud
Cache for the publish/subscribe implementation.

In the Cloud Cache example, a Region will represent the equivalent of a PatternTopic as defined in the Redis example. 


## How to Convert from Redis to Cloud Cache

### Update `build.gradle`
The Spring Boot Redis dependencies need to be updated to use Cloud Cache.

Remove these Redis dependencies:

```java
implementation "org.springframework.boot:spring-boot-starter-data-redis"
```

Replace them with this Cloud Cache dependency:

```java
implementation 'org.springframework.geode:spring-gemfire-starter:1.2.2.RELEASE'
```

To utilize the Cloud Cache dependencies, you must add the credentials and the url for the
[Pivotal Maven Commercial repo](https://commercial-repo.pivotal.io/login/auth) to
the repositories block. The resulting repositories section should look something like this:

```java
repositories {
    mavenCentral()
    maven {
        credentials {
            username "$gemfireReleaseRepoUser"
            password "$gemfireReleaseRepoPassword"
        }
        url "https://commercial-repo.pivotal.io/data3/gemfire-release-repo/gemfire"
    }
}
```

### Update `gradle.properties`
Add your [Pivotal Maven Commercial repo](https://commercial-repo.pivotal.io/login/auth) username and password to the 
`gradle.properties` file:

```properties
gemfireReleaseRepoUser=<USERNAME>
gemfireReleaseRepoPassword=<PASSWORD>
```

Replace `<USERNAME>` with your username and `<PASSWORD>` with your password. If you do not have a username and 
password, register [here](https://commercial-repo.pivotal.io/login/auth) to get an account.

### Add `@EnableClusterAware` and `@EnableCachingDefinedRegions`
In your main application or config class (in this example `Application.java`), import and add the `@EnableClusterAware` 
annotations:

```java
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.geode.config.annotation.EnableClusterAware;

@SpringBootApplication
@EnableCachingDefinedRegions
@EnableClusterAware
public class Application {
...
```

### Replace Redis Beans
The Redis example uses the following Beans to implement the pub/sub pattern on the Pivotal Platform. In the
RedisMessageListenerContainer Bean, the application defines the “chat” topic. 

```java
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
```

Replace the above Redis Beans and their imports with the following Cloud Cache equivalents in the Application.java
class:
```java
...

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.RegionConfigurer;

...

    @Bean
    CacheListenerAdapter<String, String> chatRegionSubscriber(CountDownLatch countDownLatch) {
        return new CacheListenerAdapter<String, String>() {
            public void afterCreate(EntryEvent<String, String> event) {
                LOGGER.info("Subscriber received <" + event.getNewValue() + ">");
                countDownLatch.countDown();
            }
        };
    }

    @Bean
    RegionConfigurer regionConfigurer(CacheListener<String, String> chatRegionSubscriber) {
        return new RegionConfigurer() {
            @Override
            public void configure(String regionName, ClientRegionFactoryBean<?, ?> regionFactory) {
                if (regionName.equals("chat")) {
                    LOGGER.info("Attaching cache listener to region: " + regionName);
                    regionFactory.setCacheListeners(
                            new CacheListener[]{chatRegionSubscriber});
                }
            }
        };
    }
```

The chatRegionSubscriber creates a CacheListenerAdapter that logs when the afterCreate method is triggered on a
region.  

The RegionConfigurer sets the chatRegionSubscriber as a CacheListener on the "chat" region when that region is
created.


### Update the `main` Method in `Application.java`

The Redis example uses this code to publish a message:

```java
StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
CountDownLatch countDownLatch = applicationContext.getBean(CountDownLatch.class);

LOGGER.info("Sending message...");
stringRedisTemplate.convertAndSend("chat", "Hello from Redis!");
```

The Cloud Cache application calls the sendMessage method on the MessageSender class to add an entry to the region that
the cache listener is subscribed to:

```java
CountDownLatch countDownLatch = applicationContext.getBean(CountDownLatch.class);
MessageSender messageSender = applicationContext.getBean(MessageSender.class);

LOGGER.info("Sending message...");
messageSender.sendMessage("Hello from Cloud Cache!");

```

### Remove `Subscriber.java`

This is not needed for Cloud Cache; the subscriber is a CacheListenerAdapter bean in this application (as described above).

### Add a MessageSender class

The Cloud Cache app uses the @Cacheable annotation in MessageSender.java to create a region (named “chat”) that caches
messages:
```java
...

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    @Cacheable("chat")
    public String sendMessage(String message) {
        return message;
    }
}
```

### Optional/Housekeeping
For most projects, the following changes will not be necessary, but in this example the Cloud Cache application is a
separate, self-contained project and these tweaks were needed:

- In `settings.gradle`, update the `rootProject.name` from `pubsub.redis` to `pubsub.cloudcache`.
- In `manifest.yml`, update the JAR name in `path` from `pubsub.redis` to `pubsub.cloudcache`.

## Running the Cloud Cache Application
Navigate to the Cloud Cache application directory and execute the following command:
```bash
./gradlew bootRun
```

Inspect the logs in the terminal. You should see a sequence of log entries like:

```bash
--- [main] o.s.d.g.client.ClientRegionFactoryBean   : Creating client Region [Messages]
--- [main] pubsub.cloudcache.Application            : Started Application in 2.204 seconds (JVM running for 2.555)
--- [main] pubsub.cloudcache.Application            : Sending message...
--- [main] pubsub.cloudcache.Application            : Subscriber received <Hello from Cloud Cache!>
```

After sending and receiving the message, the application will exit.

**Note:** When running these examples on the Pivotal Platform, you will need to update the manifest.yml file to bind to your
Redis or [Cloud Cache](https://docs.pivotal.io/cloud-cache-dev/get-started#test-pas) service instance.

## Notes on Testing
For these applications, the intention was to demonstrate how to migrate from Redis to Cloud Cache.  If your tests are 
not specific to either framework, they should still pass once you've migrated.