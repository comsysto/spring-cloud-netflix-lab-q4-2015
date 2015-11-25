package com.comsysto.netflix.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataPointList {
    private final List<DataPoint> list;

    @JsonCreator
    public DataPointList(@JsonProperty("list") List<DataPoint> list) {
        this.list = list;
    }

    public List<DataPoint> getList() {
        return list;
    }
}
