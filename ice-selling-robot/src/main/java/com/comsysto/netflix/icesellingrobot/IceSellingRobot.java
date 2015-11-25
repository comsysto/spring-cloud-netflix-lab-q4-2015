package com.comsysto.netflix.icesellingrobot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the current state of the robot.
 */
public class IceSellingRobot {

    private static final Logger LOGGER = LoggerFactory.getLogger(IceSellingRobot.class);

    private static final long REFILL_AMOUNT = 10_000;

    private long totalSoldAmount = 0;
    private long remainingStockAmount = 1_000;

    public synchronized long getTotalSoldAmount() {
        return totalSoldAmount;
    }

    public synchronized long getRemainingStockAmount() {
        return remainingStockAmount;
    }

    public synchronized void sell(long amount) {
        if (amount > remainingStockAmount) {
            LOGGER.warn("cannot sell {} units, only have {} left - selling all I have", amount, remainingStockAmount);
            totalSoldAmount += remainingStockAmount;
            remainingStockAmount = 0;
            return;
        }
        totalSoldAmount += amount;
        remainingStockAmount -= amount;
        LOGGER.info("sold {} units", amount);
    }

    public synchronized void refill() {
        remainingStockAmount += REFILL_AMOUNT;
    }
}
