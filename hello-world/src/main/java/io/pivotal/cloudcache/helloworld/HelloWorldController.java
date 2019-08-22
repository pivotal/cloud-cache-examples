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
