package com.comsysto.netflix.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

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
