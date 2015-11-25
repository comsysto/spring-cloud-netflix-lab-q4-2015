package com.comsysto.netflix.aggregator.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.comsysto.netflix.common.model.Country;
import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.common.model.Location;
import com.comsysto.netflix.common.model.Report;
import com.google.common.collect.Maps;

@RestController
public class AggregatorController {

    @Autowired
    private LocationServiceClient locationService;
    
    @Autowired
    private DataServiceClient dataServiceClient;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Report getReport() {
    	List<Location> locations = locationService.getAllLocations();
    	
    	Map<Country, Map<DataType, BigInteger>> map = Maps.newHashMap();
    	
    	for (Location location: locations) {
    		Country country = location.getCountry();
			if (!map.containsKey(country)) {
    			map.put(country, Maps.newHashMap());
    		}
    		
    		for (DataType dataType: DataType.values()) {
    			BigInteger sum = map.get(country).get(dataType);
    			if (sum==null) {
    				sum = BigInteger.ZERO;
    			}
    			DataPoint dataPoint = dataServiceClient.getDataPoint(location, dataType);
    			sum = sum.add(dataPoint.getValue().toBigInteger());
    			
    			map.get(country).put(dataType, sum);
    		}
    	}
    	
    	return new Report(new Date(), map);
    }
}
