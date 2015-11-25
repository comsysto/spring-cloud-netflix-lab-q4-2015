package com.comsysto.netflix.icesellingrobot;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataPointKey;
import com.comsysto.netflix.common.model.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Random;

@EnableEurekaClient
@SpringBootApplication
@RestController
@EnableAutoConfiguration
@Profile("dev")
@EnableScheduling
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static final Random RANDOM = new Random();

    @Autowired
    private RestTemplate restTemplate;

    @Value("${data.source.location}")
    private String locationId;
    @Value("${data.source.success.rate}")
    private int successRate;

    private final IceSellingRobot robot = new IceSellingRobot();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Scheduled(fixedDelay = 1_000)
    public void sellAndReport() {
        // always sell
        robot.sell(randomAmount());

        // now try to communicate
        if (RANDOM.nextInt(100) > successRate) {
            return;
        }

        send(new DataPoint(
                new DataPointKey(System.currentTimeMillis(), locationId, DataType.TOTAL_SOLD_AMOUNT),
                BigDecimal.valueOf(robot.getTotalSoldAmount())
        ));

        send(new DataPoint(
                new DataPointKey(System.currentTimeMillis(), locationId, DataType.REMAINING_STOCK_AMOUNT),
                BigDecimal.valueOf(robot.getRemainingStockAmount())
        ));
    }

    private void send(DataPoint data) {
        LOGGER.info("put data for http://current-data-service/{}/{} = {}", data.getKey().getType(), data.getKey().getLocationId(), data.getValue());
        restTemplate.put("http://current-data-service/{type}/{locationId}", data, data.getKey().getType(), data.getKey().getLocationId());
    }

    @Scheduled(fixedDelay = 20_000)
    public void refill() {
        robot.refill();
    }

    private long randomAmount() {
        return RANDOM.nextInt(1000);
    }

}
