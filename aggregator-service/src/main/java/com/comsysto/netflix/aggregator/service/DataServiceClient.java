package com.comsysto.netflix.aggregator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Location;

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
		DataPoint dataPoint = restTemplate.getForObject(
				"http://current-data-service/{}/{}", DataPoint.class, dataType, location.getId());
		return dataPoint;
	}
	
	private class DataServiceException extends RuntimeException {

		public DataServiceException(String message, Throwable cause) {
			super(message, cause);
		}	
	}
}
