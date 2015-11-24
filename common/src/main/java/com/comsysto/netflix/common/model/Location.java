package com.comsysto.netflix.common.model;

/**
 * Represents a location any agent can belong to.
 */
public class Location {
    private final String id;
    private final String city;
    private final String country;

    public Location(String id, String city, String country) {
        this.id = id;
        this.city = city;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
