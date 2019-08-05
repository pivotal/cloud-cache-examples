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
        String dataToReturn = "";
        String key = "Hello World";

        long timeDiff = System.currentTimeMillis();

        String helloValue = helloWorldService.getHelloValue(key);

        timeDiff = System.currentTimeMillis() - timeDiff;

        dataToReturn = "Key: " + key + "  Value: " + helloValue;
        dataToReturn += "\nTime to look up the value: " + timeDiff + " msec" ;

        return dataToReturn;
    }
}
