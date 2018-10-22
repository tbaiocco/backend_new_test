package com.glovoapp.backender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {

	private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final ConfigUtils configUtils;
    
    @Autowired
    public MainService(OrderRepository orderRepository, CourierRepository courierRepository, ConfigUtils configUtils) {
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
		this.configUtils = configUtils;
	}
    
    public Map<Long, List<Order>> processRequest(String courierId) {
    	Courier courier = this.getCourier(courierId);
    	
    	List<Order> filtered = this.filterOrderList(courier);
    	
    	Map<Long, List<Order>> groupByDistance = 
    			filtered
    			.stream()
    			.sorted(this.organizeInnerSorting(courier)).parallel()
    			.collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / configUtils.getDistanceSplitter()).longValue();	
    			}));
    	
        return groupByDistance;
    }
    
    public List<OrderVM> convert(List<Order> in) {
    	return in.stream()
    			.map(o -> new OrderVM(o.getId(), o.getDescription()))
    			.collect(Collectors.toList())
    			;
    }
    
    public Courier getCourier(String courierId) {
    	return courierRepository.findById(courierId);
    }
    
    private Comparator<Order> distanceSorter(Courier courier) {
    	Comparator<Order> sortDistance = (o1, o2) -> {
    		return new Double(DistanceCalculator.calculateDistance(o1.getPickup(), courier.getLocation()))
    				.compareTo(new Double(DistanceCalculator.calculateDistance(o2.getPickup(), courier.getLocation())));
    	};
    	
    	return sortDistance;
    }
    
    private Comparator<Order> vipSorter() {
    	return Comparator.comparing(Order::getVip).reversed();
    }
    
    private Comparator<Order> foodSorter() {
    	return Comparator.comparing(Order::getFood).reversed();
    }
    
    public Comparator<Order> organizeInnerSorting(Courier courier) {
    	List<Comparator<Order>> comparators = new ArrayList<>();
    	comparators.add(vipSorter()); //default 1st
    	comparators.add(foodSorter()); //default 2nd
    	comparators.add(distanceSorter(courier)); //default 3rd
    	
    	if(configUtils.getSortOrder() != null) {
    		//using custom order
    		comparators = new ArrayList<>();
    		Iterator<String> it = Arrays.asList(configUtils.getSortOrder()).iterator();
    		while(it.hasNext()) {
    			String item = it.next();
    			if(item.equalsIgnoreCase("distance"))
    				comparators.add(distanceSorter(courier));
    			if(item.equalsIgnoreCase("food"))
    				comparators.add(foodSorter());
    			if(item.equalsIgnoreCase("vip"))
    				comparators.add(vipSorter());
    		}
    	}
    	
    	return comparators.get(0)
    			.thenComparing(comparators.get(1))
				.thenComparing(comparators.get(2));
    	
    }
    
    public List<Order> filterOrderList(Courier courier) {

    	Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).map(w -> w.toLowerCase()).parallel().noneMatch(o.getDescription().toLowerCase()::contains) || courier.getBox();
    	
    	Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < configUtils.getBikeRange() || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	List<Order> initial = orderRepository.findAll();
    	
    	return initial.stream()
    			.filter(predicate.and(predicate2))
    			.collect(Collectors.toList());
    }
}
