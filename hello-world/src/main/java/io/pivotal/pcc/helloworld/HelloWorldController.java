package io.pivotal.pcc.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @Autowired
    HelloWorldService helloWorldService;

    @RequestMapping("/Hello")
    public String getHelloValue() {

        String key = "hello World";

        long timeBeforeQuery = System.currentTimeMillis();

        String helloValue = helloWorldService.getHelloValue(key);

        long timeElapsed = System.currentTimeMillis() - timeBeforeQuery;

        return "{"
                    + "\"key\":\"" + key
                    + "\",\"value\":\""
                    + helloValue + "\",\"lookupTime\":" + timeElapsed + " ms"
                +"}";
    }
}
