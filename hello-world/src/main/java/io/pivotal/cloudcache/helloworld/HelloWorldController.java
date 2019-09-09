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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Autowired
    HelloWorldService helloWorldService;

    @RequestMapping(value="/hello", produces = "text/html")
    public String getHelloValue() {

        String key = "hello";

        long timeBeforeQuery = System.currentTimeMillis();

        String helloValue = helloWorldService.getHelloValue(key);

        long timeElapsed = System.currentTimeMillis() - timeBeforeQuery;

        return "<html><body>"
                + "<i>key:</i> " + key + "<br>"
                + "<i>value:</i> " + helloValue + "<br>"
                + "<i>time to look up:</i> <b>" + timeElapsed + "ms</b>"
                + "</body></html>";
    }
}
