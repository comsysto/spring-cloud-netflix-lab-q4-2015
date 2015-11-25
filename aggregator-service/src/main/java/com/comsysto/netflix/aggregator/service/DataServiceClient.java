package com.comsysto.netflix.aggregator.service;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class DataServiceClient {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataServiceClient.class);
	
	private static final int THREAD_SLEEP_TIME=1000;

	@Autowired
	private RestTemplate restTemplate;
	
	public DataPoint getDataPoint(Location location, DataType dataType) {
		LOGGER.info("fetching data point from http://current-data-service/{}/{}", dataType, location.getId());
		try {
			Thread.sleep(THREAD_SLEEP_TIME);
		} catch (InterruptedException e) {
			throw new DataServiceException("could not fetch data point for location "+location+" and dataType "+dataType, e);
		}
		try {
			return restTemplate.getForObject("http://current-data-service/{type}/{location}", DataPoint.class, dataType, location.getId());
		} catch (HttpClientErrorException e) {
			// TODO use hystrix instead?
			return null;
		}
	}
	
	private class DataServiceException extends RuntimeException {

		public DataServiceException(String message, Throwable cause) {
			super(message, cause);
		}	
	}
}
