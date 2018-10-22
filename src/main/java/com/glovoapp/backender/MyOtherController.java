package com.glovoapp.backender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class MyOtherController {

	private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    @Autowired
    private MainService mainService;
    @Autowired
    private ConfigUtils configUtils;    
    
    @Autowired
    public MyOtherController(OrderRepository orderRepository, CourierRepository courierRepository/*, MainService mainService, ConfigUtils configUtils*/) {
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
//		this.mainService = mainService;
//		this.configUtils = configUtils;
	}
    
    @GetMapping("/config")
	String showConfig() {
		return configUtils.toString();
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
    
    @GetMapping(value="/test/filtered/{courierId}")
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
    
    @GetMapping(value="/test/info/{courierId}")
    @ResponseBody
    Map<Long, List<OrderVMTeo>> ordersForCourierExtInfo(@PathVariable String courierId) {

    	//this is a first try, doing a full method
    	Courier courier = courierRepository.findById(courierId);
    	
    	Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).map(w -> w.toLowerCase()).parallel().noneMatch(o.getDescription().toLowerCase()::contains) || courier.getBox();
    	
    	Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) < configUtils.getBikeRange() || courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
    	
    	List<Order> initial = orderRepository.findAll();
    	
    	Map<Long, List<OrderVMTeo>> groupByDistance = 
    			initial
    			.stream()
    			.filter(predicate.and(predicate2))
    			.sorted(mainService.organizeInnerSorting(courier)).parallel()
    			.map(order -> new OrderVMTeo(order, DistanceCalculator.calculateDistance(order.getPickup(), courier.getLocation())))
    			.collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / configUtils.getDistanceSplitter()).longValue();	
    			}));
    	
        return groupByDistance;
    }
}
