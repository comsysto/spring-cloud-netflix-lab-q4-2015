package com.comsysto.netflix.data.current.web;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.data.current.repository.CurrentDataRepository;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CurrentDataController {

    @Autowired
    private CurrentDataRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<DataPoint> fetchAll() {
        return repository.fetchAll();
    }

    @RequestMapping(value = "/{type}/{locationId}", method = RequestMethod.GET)
    public DataPoint get(@PathVariable("type") DataType type,
                         @PathVariable("locationId") String locationId) {
        DataPoint dataPoint = repository.get(type, locationId);
        if (dataPoint == null) {
            throw new NotFoundException();
        }
        return dataPoint;
    }

    @RequestMapping(value = "/{type}/{locationId}", method = RequestMethod.PUT)
    public void put(@PathVariable("type") DataType type,
                    @PathVariable("locationId") String locationId,
                    @RequestBody DataPoint dataPoint) {
        //check for consistency
        Preconditions.checkArgument(type.equals(dataPoint.getKey().getType()));
        Preconditions.checkArgument(locationId.equals(dataPoint.getKey().getLocationId()));

        repository.put(dataPoint);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
        // nothing to do
    }

    private static final class NotFoundException extends RuntimeException {
    }

}
