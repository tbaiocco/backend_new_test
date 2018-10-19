package com.glovoapp.backender;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class MainController {

	private final String welcomeMessage;
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    
    @Autowired
    ConfigUtils configUtils;
    
    @Autowired
    public MainController(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository, CourierRepository courierRepository) {
		this.welcomeMessage = welcomeMessage;
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
	}
    
    @GetMapping("/config")
    String test() {
    	return welcomeMessage
    			.concat("\n")
    			.concat(configUtils.toString())
    			.concat("\n")
    			.concat("other vars");
    }
    
    @GetMapping("/couriers")
    @ResponseBody
    String couriers() {
    	return new Gson()
    			.toJson(
    					courierRepository.findAll()
    					.stream()
    					.collect(Collectors.toSet()));
    }
    
    @GetMapping("/couriers/{id}")
    String getCourier(@PathVariable("id") String courierId) {
        return new Gson()
    			.toJson(courierRepository.findById(courierId));
    }        
    
    @GetMapping(value="/orders/filtered/{courierId}")
    @ResponseBody
    List<OrderVM> ordersForCourierFiltered(@PathVariable String courierId) {

    	//this is a first try, doing a full method
    	Courier courier = courierRepository.findById(courierId);

    	Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).map(w -> w.toLowerCase()).parallel().noneMatch(o.getDescription().toLowerCase()::contains) || courier.getBox();
    	
    	Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < configUtils.getBikeRange() || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	List<Order> initial = orderRepository.findAll();
    	
    	List<OrderVM> out = initial
                .stream()
                .filter(predicate.and(predicate2))
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    	
        return out;
    }
    
    @Deprecated
    public void filterByContent(Stream<Order> orders, Courier courier) {
    	
    	Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).parallel().noneMatch(o.getDescription()::contains) || courier.getBox();
    	
    	orders.filter(predicate);
    	
    }
    
    @Deprecated
    public void filterByDistance(Stream<Order> orders, Courier courier) {
    	
    	Predicate<Order> predicate = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < 5d || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	orders.filter(predicate);
    	
    }

    @GetMapping(value="/orders/{courierId}")
    @ResponseBody
    Map<Long, List<OrderVMTeo>> ordersForCourier(@PathVariable String courierId) {

    	//this is a first try, doing a full method
    	Courier courier = courierRepository.findById(courierId);
    	
    	Comparator<Order> sortDistance = (o1, o2) -> {
    		return new Double(DistanceCalculator.calculateDistance(o1.getPickup(), courier.getLocation()))
    				.compareTo(new Double(DistanceCalculator.calculateDistance(o2.getPickup(), courier.getLocation())));
    	};
    	    	
    	Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).map(w -> w.toLowerCase()).parallel().noneMatch(o.getDescription().toLowerCase()::contains) || courier.getBox();
    	
    	Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < configUtils.getBikeRange() || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	List<Order> initial = orderRepository.findAll();
    	
    	Map<Long, List<OrderVMTeo>> groupByDistance = 
    			initial
    			.stream()
    			.filter(predicate.and(predicate2))
    			.sorted(Comparator.comparing(Order::getVip).reversed()
    					.thenComparing(Comparator.comparing(Order::getFood).reversed())
    					.thenComparing(sortDistance)).parallel()
    			.map(order -> new OrderVMTeo(order, DistanceCalculator.calculateDistance(order.getPickup(), courier.getLocation())))
    			.collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / configUtils.getDistanceSplitter()).longValue();	
    			}));
    	
        return groupByDistance;
    }
}
