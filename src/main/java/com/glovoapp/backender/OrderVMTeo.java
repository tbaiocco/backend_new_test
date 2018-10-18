package com.glovoapp.backender;

public class OrderVMTeo extends OrderVM {
	
	private double distance;
	private Location pickup;
	private Boolean vip;
	private Boolean food;

	public OrderVMTeo(String id, String description, double distance) {
		super(id, description);
		this.distance = distance;
	}

	public OrderVMTeo(Order order, double distance) {
		super(order.getId(), order.getDescription());
		this.pickup = order.getPickup();
		this.vip = order.getVip();
		this.food = order.getFood();
		this.distance = distance;
	}
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Location getPickup() {
		return pickup;
	}

	public void setPickup(Location pickup) {
		this.pickup = pickup;
	}

	public Boolean getVip() {
		return vip;
	}

	public void setVip(Boolean vip) {
		this.vip = vip;
	}

	public Boolean getFood() {
		return food;
	}

	public void setFood(Boolean food) {
		this.food = food;
	}

}
