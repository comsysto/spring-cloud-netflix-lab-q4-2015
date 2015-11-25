package com.comsysto.netflix.location.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.comsysto.netflix.common.model.Country;
import com.comsysto.netflix.common.model.Location;
import com.google.common.collect.Lists;

@Repository
public class LocationRepositoryImpl implements LocationRepository {
 
    private static final List<Location> data;
    
    static {
    	data = Lists.newArrayList();
    	data.add(new Location("MUC", "Munich", new Country("Germany")));
    	data.add(new Location("FRA", "Frankfurt", new Country("Germany")));
    	data.add(new Location("BER", "Berlin", new Country("Germany")));
    	data.add(new Location("WIE", "Wien", new Country("Austria")));
    	data.add(new Location("PAR", "Paris", new Country("France")));
    	data.add(new Location("LON", "London", new Country("England")));
    	data.add(new Location("HAV", "Havanna", new Country("Cuba")));
    	data.add(new Location("TIM", "Timbuktu", new Country("Mali")));
    }

	@Override
	public List<Location> fetchAll() {
		return data;
	}
}
