/*
 * Cloud Cache Examples
 *
 * Copyright (c) 2019-Present Pivotal Software, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License"). You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package io.pivotal.cloudcache.helloworld;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class HelloWorldService {

    // @Cacheable uses the parameter of the method it annotates as the key and caches the return value.
    // (Region with name "Hello" will be created via @EnableCachingDefinedRegions on Application)
    @Cacheable("Hello")
    // This method will not be invoked if the key is in the cache
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
