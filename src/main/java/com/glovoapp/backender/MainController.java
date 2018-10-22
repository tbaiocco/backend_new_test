package com.glovoapp.backender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final MainService service;
    
    @Autowired
    public MainController(MainService mainService) {
		this.service = mainService;
	}
    
    @GetMapping(value="/orders/{courierId}")
    @ResponseBody
    Map<Long, List<OrderVM>> ordersForCourier(@PathVariable String courierId) {
    	//Process data
    	Map<Long, List<Order>> filtered = service.processRequest(courierId);
    	
    	//Convert to view
    	Map<Long, List<OrderVM>> groupByDistance = 
    			filtered
    			.entrySet()
    			.stream()
    			.collect(Collectors.toMap(
    					e -> e.getKey(), 
    					e -> (List<OrderVM>)service.convert(e.getValue()))
    			);
    			
        return groupByDistance;
    }

}
