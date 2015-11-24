package com.comsysto.netflix.data.source.totalsales;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataPointKey;
import com.comsysto.netflix.common.model.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Random;

@EnableEurekaClient
@SpringBootApplication
@EnableScheduling
public class Application {

    public static final Random RANDOM = new Random();

    @Autowired
    private RestTemplate restTemplate;
    @Value("${data.source.location}}")
    private String locationId;
    @Value("${data.source.success.rate}")
    private int successRate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Scheduled(fixedDelay = 1000)
    public void storeCurrentData() {
        if (RANDOM.nextInt(100) > successRate) {
            return;
        }

        DataType type = DataType.TOTAL_SALES;
        DataPoint data = new DataPoint(
                new DataPointKey(System.currentTimeMillis(), locationId, type),
                randomData()
        );

        restTemplate.put("http://current-data-service/{type}/{locationId}", data, type, locationId);
    }

    private BigDecimal randomData() {
        return BigDecimal.valueOf(RANDOM.nextInt(1000));
    }

}