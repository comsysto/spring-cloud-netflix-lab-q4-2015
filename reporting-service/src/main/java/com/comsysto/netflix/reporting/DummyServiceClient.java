package com.comsysto.netflix.reporting;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DummyServiceClient {
	
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod="fallbackReport")
	public String callDummy() {
		return restTemplate.getForObject("http://dummy-service/dummy", String.class);
	}

	public String fallbackReport() {
	    	return "ich bin ein fallback report";
	    }
}
