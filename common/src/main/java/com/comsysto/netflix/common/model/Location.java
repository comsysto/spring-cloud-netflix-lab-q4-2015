package com.comsysto.netflix.common.model;

/**
 * Represents a location any agent can belong to.
 */
public class Location {
    private final String id;
    private final String city;
    private final Country country;

    public Location(String id, String city, Country country) {
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

    public Country getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
