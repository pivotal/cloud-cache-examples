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

        // {"key":"hello","value":"First called at 123","lookupTime":3717}

        return "Key: " + key + "<br>  Value: '" + helloValue + "' "
            + "<br>Time to look up the value: " + timeDiff + " msec" ;
    }
}
