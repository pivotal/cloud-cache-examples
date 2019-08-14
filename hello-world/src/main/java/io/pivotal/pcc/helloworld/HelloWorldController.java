package io.pivotal.pcc.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @Autowired
    HelloWorldService helloWorldService;

    @RequestMapping("/hello")
    public String getHelloValue() {
        String key = "Hello World";

        long timeDiff = System.currentTimeMillis();

        String helloValue = helloWorldService.getHelloValue(key);

        timeDiff = System.currentTimeMillis() - timeDiff;

        return "{\"key\":\"" + key + "\",\"value\":\"" + helloValue + "\",\"lookupTime\":" + timeDiff + "}";
    }
}
