package com.comsysto.netflix.report.job;

import com.comsysto.netflix.common.model.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient
@SpringBootApplication
@EnableScheduling
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final int JOB_RUN_INTERVAL_IN_SECONDS = 30;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Scheduled(fixedDelay = JOB_RUN_INTERVAL_IN_SECONDS * 1000)
    public void updateCachedReport() {
        LOGGER.info("fetching all data from http://aggregator-service/");
        Report report = restTemplate.getForObject("http://aggregator-service/", Report.class);

        LOGGER.info("put data to http://aggregated-report-cache/");
        restTemplate.put("http://aggregated-report-cache/", report);
    }

}
