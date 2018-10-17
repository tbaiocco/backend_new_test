package com.glovoapp.backender;

import java.util.Objects;

class Location {
    private Double lat;
    private Double lon;

    Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    Double getLat() {
        return lat;
    }

    Double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(lat, location.lat) &&
                Objects.equals(lon, location.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }

    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
