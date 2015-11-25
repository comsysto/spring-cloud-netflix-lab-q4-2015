package com.comsysto.netflix.aggregator.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.comsysto.netflix.common.model.Location;
import com.comsysto.netflix.common.model.LocationList;

@Component
public class LocationServiceClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LocationServiceClient.class);

	@Autowired
	private RestTemplate restTemplate;

	public List<Location> getAllLocations() {
		LOGGER.info("fetching locations from http://location-service/");
		LocationList locations = restTemplate.getForObject(
				"http://location-service/", LocationList.class);
		return locations.getList();
	}
}
