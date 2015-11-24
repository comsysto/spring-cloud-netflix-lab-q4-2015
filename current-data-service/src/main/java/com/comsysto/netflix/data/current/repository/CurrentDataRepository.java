package com.comsysto.netflix.data.current.repository;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;

import java.util.List;

/**
 * Holds a single value per key, no historical data.
 * Data that is older than the outdatedThreshold is ignored.
 */
public interface CurrentDataRepository {
    /**
     * How long will a stored data point remain valid?
     */
    int getOutdatedThresholdInSeconds();

    /**
     * Get the data point for the given parameters. <code>null</code> if no up-to-date value is present.
     */
    DataPoint get(DataType type, String locationId);

    /**
     * Store a fresh data point. Overwrite any present data.
     */
    void put(DataPoint dataPoint);

    /**
     * Fetch all valid data we have in the store right now.
     */
    List<DataPoint> fetchAll();
}
