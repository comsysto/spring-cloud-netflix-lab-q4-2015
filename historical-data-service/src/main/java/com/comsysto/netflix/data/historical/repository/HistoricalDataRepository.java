package com.comsysto.netflix.data.historical.repository;

import com.comsysto.netflix.common.model.DataPoint;
import com.comsysto.netflix.common.model.DataType;

import java.util.List;

/**
 * Holds all known historical values per type and location.
 */
public interface HistoricalDataRepository {

    /**
     * Get all data points for the given parameters.
     * Result is sorted, latest data point at index 0.
     */
    List<DataPoint> get(DataType type, String locationId);

    /**
     * Get latest data point for the given parameters.
     */
    DataPoint getLatest(DataType type, String locationId);

    /**
     * Store a fresh data point.
     */
    void add(DataPoint dataPoint);
}
