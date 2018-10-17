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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class MainController {

	private final String welcomeMessage;
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    
    private final String[] exclude = {"pizza","cake","flamingo"}; 

    @Value("${backender.box_keywords:}") 
    private String[] needsBox; 
    
    @Value("${backender.bike_range:5}") 
    private Integer bikeRange;
    
    @Autowired
    public MainController(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository, CourierRepository courierRepository) {
		this.welcomeMessage = welcomeMessage;
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
	}
    
    @GetMapping("/test")
    String test() {
    	return welcomeMessage
    			.concat(", Configs:\n")
    			.concat("("+needsBox.length+") box keywords:"+Arrays.toString(needsBox))
    			.concat("\n")
    			.concat("bike range (km):"+bikeRange.toString())
    			.concat("\n")
    			.concat("other vars");
    }
    
    @RequestMapping("/couriers")
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

    	Predicate<Order> predicate = o -> Arrays.stream(exclude).map(w -> w.toLowerCase()).parallel().noneMatch(o.getDescription().toLowerCase()::contains) || courier.getBox();
    	
//    	Predicate<Order> predicate = o -> Arrays.stream(exclude).parallel().noneMatch(o.getDescription()::contains) || courier.getBox();
    	
    	Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < 5d || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	List<Order> initial = orderRepository.findAll();
    	
    	System.out.println("Init size:"+initial.size());
    	
    	List<OrderVM> out = initial
                .stream()
                .filter(predicate.and(predicate2))
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    	
    	System.out.println("Out size:"+out.size()); 
    		
        return out;
    }
    
    private void filterByContent(Stream<Order> orders, Courier courier) {
    	
    	Predicate<Order> predicate = o -> Arrays.stream(exclude).parallel().noneMatch(o.getDescription()::contains) || courier.getBox();
    	
    	orders.filter(predicate);
    	
    }
    
    private void filterByDistance(Stream<Order> orders, Courier courier) {
    	
    	Predicate<Order> predicate = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < 5d || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	orders.filter(predicate);
    	
    }

    @GetMapping(value="/orders/{courierId}")
    @ResponseBody
    List<OrderVM> ordersForCourier(@PathVariable String courierId) {

    	//this is a first try, doing a full method
    	Courier courier = courierRepository.findById(courierId);
    	
    	Comparator<Order> sort = (o1, o2) -> {
    		return new Double(DistanceCalculator.calculateDistance(o1.getPickup(), courier.getLocation()))
    				.compareTo(new Double(DistanceCalculator.calculateDistance(o2.getPickup(), courier.getLocation())));
    	};
    	
    	String[] exclude = {"pizza","cake","flamingo"}; 
    	
    	Predicate<Order> predicate = o -> Arrays.stream(exclude).map(w -> w.toLowerCase()).parallel().noneMatch(o.getDescription().toLowerCase()::contains) || courier.getBox();
    	
    	Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < 5d || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	List<Order> initial = orderRepository.findAll();
    	
    	System.out.println("Init size:"+initial.size());
    	
    	List<OrderVM> out = initial
                .stream()
                .filter(predicate.and(predicate2))
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    	
    	System.out.println("Out size:"+out.size());
    	
    	Map<Long, List<Order>> groupByDistance = 
    			initial.stream().collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / 0.5).longValue();	
    			}));
    	
    	System.out.println(groupByDistance);
    	
    	Map<Long, Map<String, Map<String, List<Order>>>> groupByVipAndFood = 
    			initial.stream().collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / 0.5).longValue();	
    			}, Collectors.groupingBy(o -> {
    				return new String(o.getVip() ? "VIP" : "Normal");	
    			}, Collectors.groupingBy(o -> {
    				return new String(o.getFood() ? "Food" : "Other");	
    			}))));
    	
    	System.out.println(groupByVipAndFood);
    	
    	Map<Long, Map<String, Map<String, List<Order>>>> groupedSorted = 
    			initial.stream()
    			.sorted(sort).parallel()
    			.collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / 0.5).longValue();	
    			}, Collectors.groupingBy(o -> {
    				return new String(o.getVip() ? "VIP" : "Normal");	
    			}, Collectors.groupingBy(o -> {
    				return new String(o.getFood() ? "Food" : "Other");	
    			}))));
    	
    	System.out.println(groupedSorted);
    	
    	
        return out;
    }
}
