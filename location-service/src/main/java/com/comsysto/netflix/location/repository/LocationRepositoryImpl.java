package com.comsysto.netflix.location.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.comsysto.netflix.common.model.Location;
import com.google.common.collect.Lists;

@Repository
public class LocationRepositoryImpl implements LocationRepository {
 
    private static final List<Location> data;
    
    static {
    	data = Lists.newArrayList();
    	data.add(new Location("MUC", "Munich", "Germany"));
    	data.add(new Location("FRA", "Frankfurt", "Germany"));
    	data.add(new Location("BER", "Berlin", "Germany"));
    	data.add(new Location("WIE", "Wien", "Austria"));
    	data.add(new Location("PAR", "Paris", "France"));
    	data.add(new Location("LON", "London", "England"));
    	data.add(new Location("HAV", "Havanna", "Cuba"));
    	data.add(new Location("TIM", "Timbuktu", "Mali"));
    }

	@Override
	public List<Location> fetchAll() {
		return data;
	}
}
