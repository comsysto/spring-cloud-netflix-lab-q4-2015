package com.comsysto.netflix.data.current.web;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataPointList;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.data.current.repository.CurrentDataRepository;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CurrentDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentDataController.class);

    @Autowired
    private CurrentDataRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public DataPointList fetchAll() {
        List<DataPoint> dataPoints = repository.fetchAll();
        LOGGER.info("fetchAll returns {} data points.", dataPoints.size());
        return new DataPointList(dataPoints);
    }

    @RequestMapping(value = "/{type}/{locationId}", method = RequestMethod.GET)
    public DataPoint get(@PathVariable("type") DataType type,
                         @PathVariable("locationId") String locationId) {
        DataPoint dataPoint = repository.get(type, locationId);
        if (dataPoint == null) {
            LOGGER.info("get '{}/{}' does not find anything.", type, locationId);
            throw new NotFoundException();
        } else {
            LOGGER.info("get '{}/{}' returns valid data point: '{}'.", type, locationId, dataPoint);
            return dataPoint;
        }
    }

    @RequestMapping(value = "/{type}/{locationId}", method = RequestMethod.PUT)
    public void put(@PathVariable("type") DataType type,
                    @PathVariable("locationId") String locationId,
                    @RequestBody DataPoint dataPoint) {
        //check for consistency
        Preconditions.checkArgument(type.equals(dataPoint.getKey().getType()));
        Preconditions.checkArgument(locationId.equals(dataPoint.getKey().getLocationId()));

        LOGGER.info("put '{}/{}' with data point: '{}'.", type, locationId, dataPoint);
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
