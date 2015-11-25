package com.comsysto.netflix.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationList {
    private final List<Location> list;

    @JsonCreator
    public LocationList(@JsonProperty("list") List<Location> list) {
        this.list = list;
    }

    public List<Location> getList() {
        return list;
    }
}
