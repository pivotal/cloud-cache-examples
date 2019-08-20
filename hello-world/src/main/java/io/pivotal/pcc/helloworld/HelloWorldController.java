package io.pivotal.pcc.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Autowired
    HelloWorldService helloWorldService;

    @RequestMapping(value="/hello", produces = "application/json")
    public String getHelloValue() {

        String key = "hello World";

        long timeBeforeQuery = System.currentTimeMillis();

        String helloValue = helloWorldService.getHelloValue(key);

        long timeElapsed = System.currentTimeMillis() - timeBeforeQuery;

        return "{"
                + "\"key\":\"" + key + "\",\""
                +"value (time of initial lookup used as value in cache)\":\"" + helloValue + "\","
                +"\"timeToLookup\":\"" + timeElapsed + "ms\""
                +"}";
    }

    @RequestMapping(value="/hello-html", produces = "text/html")
    public String getHelloHtmlValue() {

        String key = "hello";

        long timeBeforeQuery = System.currentTimeMillis();

        String helloValue = helloWorldService.getHelloValue(key);

        long timeElapsed = System.currentTimeMillis() - timeBeforeQuery;

        return "<html><body>"
                + "<i>key:</i> " + key + " <i>value:</i> " + helloValue + "<br>"
                + "<i>time to look up:</i> <b>" + timeElapsed + "ms</b></body></html>";
    }
}
