package com.comsysto.netflix.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPointKey {
    private final long timestamp;
    private final String locationId;
    private final DataType type;

    @JsonCreator
    public DataPointKey(@JsonProperty("timestamp") long timestamp, @JsonProperty("locationId") String locationId, @JsonProperty("type") DataType type) {
        this.timestamp = timestamp;
        this.locationId = locationId;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLocationId() {
        return locationId;
    }

    public DataType getType() {
        return type;
    }
}
