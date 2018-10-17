package com.glovoapp.backender;

import java.util.Objects;

class Order {
    private String id;
    private String description;
    private Boolean food;
    private Boolean vip;
    private Location pickup;
    private Location delivery;

    String getId() {
        return id;
    }

    Order withId(String id) {
        this.id = id;
        return this;
    }

    String getDescription() {
        return description;
    }

    Order withDescription(String description) {
        this.description = description;
        return this;
    }

    Boolean getFood() {
        return food;
    }

    Order withFood(Boolean food) {
        this.food = food;
        return this;
    }

    Boolean getVip() {
        return vip;
    }

    Order withVip(Boolean vip) {
        this.vip = vip;
        return this;
    }

    Location getPickup() {
        return pickup;
    }

    Order withPickup(Location pickup) {
        this.pickup = pickup;
        return this;
    }

    Location getDelivery() {
        return delivery;
    }

    Order withDelivery(Location delivery) {
        this.delivery = delivery;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(description, order.description) &&
                Objects.equals(food, order.food) &&
                Objects.equals(vip, order.vip) &&
                Objects.equals(pickup, order.pickup) &&
                Objects.equals(delivery, order.delivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, food, vip, pickup, delivery);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", food=" + food +
                ", vip=" + vip +
                ", pickup=" + pickup +
                ", delivery=" + delivery +
                '}';
    }
}
