package com.comsysto.netflix.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @Autowired
    private DummyServiceClient dummyServiceClient;


    @RequestMapping("report")
    public String report() {
        return dummyServiceClient.callDummy();
    }
}
