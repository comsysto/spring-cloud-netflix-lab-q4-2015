package com.comsysto.netflix.report.cache.web;

import com.comsysto.netflix.common.model.Report;
import com.comsysto.netflix.report.cache.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReportCacheController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCacheController.class);

    @Autowired
    private ReportRepository repository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Report fetchAll() {
        Report report = repository.get();
        LOGGER.info("get report: '{}'", report);
        return report;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public void put(@RequestBody Report report) {
        LOGGER.info("put report: '{}'", report);
        repository.put(report);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
        // nothing to do
    }

    private static final class NotFoundException extends RuntimeException {
    }

}
