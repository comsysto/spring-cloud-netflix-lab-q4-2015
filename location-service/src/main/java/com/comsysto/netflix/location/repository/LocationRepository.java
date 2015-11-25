package com.comsysto.netflix.location.repository;

import com.comsysto.netflix.common.model.Location;

import java.util.List;

public interface LocationRepository {

	List<Location> fetchAll();
}
