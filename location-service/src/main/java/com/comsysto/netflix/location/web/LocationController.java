package com.comsysto.netflix.location.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.comsysto.netflix.common.model.Location;
import com.comsysto.netflix.common.model.LocationList;
import com.comsysto.netflix.location.repository.LocationRepository;

@RestController
public class LocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public LocationList fetchAll() {
        List<Location> locations = repository.fetchAll();
        LOGGER.info("fetchAll returns {} locations.", locations.size());
        return new LocationList(locations);
    }
}
