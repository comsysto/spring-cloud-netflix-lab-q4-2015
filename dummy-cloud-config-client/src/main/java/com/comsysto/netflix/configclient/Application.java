package com.comsysto.netflix.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient
@SpringBootApplication
@RestController
@EnableAutoConfiguration
@Profile("dev")
public class Application {

    @Value("${some.other.property}")
    public String someOtherProperty;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @RequestMapping("dummy")
    public String dummy() {
    	return "dummy";
    }

    @RequestMapping("foo")
    public String foo() {
        String response = someOtherProperty;
        return response;
    }
}
