package com.glovoapp.backender;

import java.util.Objects;

class Courier {
    private String id;
    private String name;
    private Boolean box;
    private Vehicle vehicle;
    private Location location;

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    Boolean getBox() {
        return box;
    }

    Vehicle getVehicle() {
        return vehicle;
    }

    Location getLocation() {
        return location;
    }

    Courier withId(String id) {
        this.id = id;
        return this;
    }

    Courier withName(String name) {
        this.name = name;
        return this;
    }

    Courier withBox(Boolean box) {
        this.box = box;
        return this;
    }

    Courier withVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    Courier withLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Courier courier = (Courier) o;
        return Objects.equals(id, courier.id) &&
                Objects.equals(name, courier.name) &&
                Objects.equals(box, courier.box) &&
                vehicle == courier.vehicle &&
                Objects.equals(location, courier.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, box, vehicle, location);
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", box=" + box +
                ", vehicle=" + vehicle +
                ", location=" + location +
                '}';
    }
}
