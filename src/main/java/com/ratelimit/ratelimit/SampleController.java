package com.ratelimit.ratelimit;

/**
 * Created by sarang on 29/05/17.
 */

import org.springframework.web.bind.annotation.*;

@RestController
public class SampleController {

    @RequestMapping("/intercept")
    public String intercept_test() {
        System.out.println("this is controller, request path is intercept");
        return "interceptor, request path is intercept";
    }

    @RequestMapping("/pay")
    public String api_pay() {
        System.out.println("this is controller, request path is pay");
        return "request is successful at pay api";
    }

    @RequestMapping("/status")
    public String api_status() {
        System.out.println("this is controller , request path is status");
        return "request is successful at status api";
    }
}
