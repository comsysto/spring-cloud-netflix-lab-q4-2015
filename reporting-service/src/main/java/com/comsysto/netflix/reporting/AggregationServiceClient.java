package com.comsysto.netflix.reporting;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.comsysto.netflix.common.model.Country;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Report;
import com.google.common.collect.Maps;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Component
public class AggregationServiceClient {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AggregationServiceClient.class);

	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(
		fallbackMethod="getCachedReport",
		commandProperties={
			@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="2"),
			@HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="10000")
		},
		threadPoolKey="getReportPool"
	)
	public Report getReport() {
		LOGGER.info("fetching data point from http://aggregator-service/");
		return restTemplate.getForObject("http://aggregator-service/", Report.class);
	}
	
	@HystrixCommand(
			fallbackMethod="createDummyReport",
			commandProperties={
				@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="2"),
				@HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="10000")
			},
			threadPoolKey="getCachedReportPool"
		)
	public Report getCachedReport() {
		LOGGER.info("fetching data point from http://aggregated-report-cache/");
		return restTemplate.getForObject("http://aggregated-report-cache/", Report.class);
	}

	public Report createDummyReport() {
        Map<DataType, BigInteger> set1 = new HashMap<>();
        set1.put(DataType.TOTAL_SOLD_AMOUNT, BigInteger.ONE);

        Map<DataType, BigInteger> set2 = new HashMap<>();
        set2.put(DataType.TOTAL_SOLD_AMOUNT, BigInteger.ZERO);

        Map<Country, Map<DataType, BigInteger>> reportData = Maps.newHashMap();
        reportData.put(new Country("Germany"), set1);
        reportData.put(new Country("Austria"), set2);
        return new Report(new Date(System.currentTimeMillis()), reportData);
    }
}
