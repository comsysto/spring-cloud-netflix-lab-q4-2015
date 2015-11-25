package com.comsysto.netflix.data.historical.web;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataPointList;
import com.comsysto.netflix.common.model.DataType;
import com.comsysto.netflix.data.historical.repository.HistoricalDataRepository;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HistoricalDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalDataController.class);

    @Autowired
    private HistoricalDataRepository repository;

    @RequestMapping(value = "/{type}/{locationId}", method = RequestMethod.GET)
    public DataPointList get(@PathVariable("type") DataType type,
                         @PathVariable("locationId") String locationId) {
        List<DataPoint> dataPoints = repository.get(type, locationId);
        LOGGER.info("get '{}/{}' returns {} data points.", type, locationId, dataPoints.size());
        return new DataPointList(dataPoints);
    }

    @RequestMapping(value = "/{type}/{locationId}/latest", method = RequestMethod.GET)
    public DataPoint getLatest(@PathVariable("type") DataType type,
                               @PathVariable("locationId") String locationId) {
        DataPoint dataPoint = repository.getLatest(type, locationId);
        if (dataPoint == null) {
            LOGGER.info("get '{}/{}/latest' does not find anything.", type, locationId);
            throw new NotFoundException();
        } else {
            LOGGER.info("get '{}/{}/latest' returns valid data point: '{}'.", type, locationId, dataPoint);
            return dataPoint;
        }
    }

    @RequestMapping(value = "/{type}/{locationId}", method = RequestMethod.POST)
    public void post(@PathVariable("type") DataType type,
                     @PathVariable("locationId") String locationId,
                     @RequestBody DataPoint dataPoint) {
        //check for consistency
        Preconditions.checkArgument(type.equals(dataPoint.getKey().getType()));
        Preconditions.checkArgument(locationId.equals(dataPoint.getKey().getLocationId()));

        LOGGER.info("post '{}/{}' with data point: '{}'.", type, locationId, dataPoint);
        repository.add(dataPoint);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
        // nothing to do
    }

    private static final class NotFoundException extends RuntimeException {
    }

}
