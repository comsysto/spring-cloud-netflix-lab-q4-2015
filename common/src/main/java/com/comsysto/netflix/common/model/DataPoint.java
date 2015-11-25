package com.comsysto.netflix.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class DataPoint {
    private final DataPointKey key;
    private final BigDecimal value;

    @JsonCreator
    public DataPoint(@JsonProperty("key") DataPointKey key, @JsonProperty("value") BigDecimal value) {
        this.key = key;
        this.value = value;
    }

    public DataPointKey getKey() {
        return key;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
