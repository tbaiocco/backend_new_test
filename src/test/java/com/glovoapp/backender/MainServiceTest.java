package com.glovoapp.backender;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes= {API.class, ConfigExtended.class})
@ExtendWith(SpringExtension.class)
class MainServiceTest {
	
	@Autowired
    private ConfigUtils configUtils;
	
	private OrderRepository orderRepository;
	private CourierRepository courierRepository;
	private MainService service;

	@Autowired
    public MainServiceTest(OrderRepository orderRepository, CourierRepository courierRepository) {
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
	}
	
	@BeforeEach
	void init() {
		service = new MainService(orderRepository, courierRepository, configUtils);
	}
	
	@Test
    @DisplayName("Checking DI on @Autowired")
    void checkResolverForAutowired() {
        assertNotNull(configUtils);
    }
	
	@Test
    @DisplayName("Checking DI on Constructor")
    void checkResolverForConstructor() {
        assertNotNull(orderRepository);
        assertNotNull(courierRepository);
        assertFalse(orderRepository.findAll().isEmpty());
        assertFalse(courierRepository.findAll().isEmpty());
        assertNotNull(service);
        assertNotNull(service.getCourier("courier-faa2186e65f2"));
        assertNotNull(service.convert(orderRepository.findAll()));
    }
	
	@Test
	final void testConvert() {
		List<Order> in = orderRepository.findAll();
		List<OrderVM> expected = in
				.stream()
				.map(o -> new OrderVM(o.getId(), o.getDescription()))
    			.collect(Collectors.toList())
    			;
		assertEquals(expected.get(0).getClass(), service.convert(in).get(0).getClass());
	}
	
	@Test
	final void testGetCourier() {
		Courier expected = courierRepository.findById("courier-faa2186e65f2");
		assertEquals(expected, service.getCourier("courier-faa2186e65f2"));
	}

	@Test
	final void testOrganizeInnerSorting() {
		Courier expected = courierRepository.findById("courier-faa2186e65f2");
		try {
			assertNotNull(service.organizeInnerSorting(expected));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	final void testFilterOrderListBikeNoBox() {
		Courier courier = courierRepository.findById("courier-7e1552836a04");
		List<Order> in = service.filterOrderList(courier);
		Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).map(w -> w.toLowerCase()).parallel().anyMatch(o.getDescription().toLowerCase()::contains) && courier.getBox();
		assertFalse(in
			.stream()
			.filter(predicate)
			.findFirst().isPresent()
			);
		
		Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) >= configUtils.getBikeRange() && courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
		assertFalse(in
			.stream()
			.filter(predicate2)
			.findFirst().isPresent()
			);
		
	}
	
	@Test
	final void testFilterOrderListScooterNoBox() {
		Courier courier = courierRepository.findById("courier-d2215386ab3b");
		List<Order> in = service.filterOrderList(courier);
		Predicate<Order> predicate = o -> Arrays.stream(configUtils.getNeedsBox()).map(w -> w.toLowerCase()).parallel().anyMatch(o.getDescription().toLowerCase()::contains) && courier.getBox();
		assertFalse(in
			.stream()
			.filter(predicate)
			.findFirst().isPresent()
			);
		
		Predicate<Order> predicate2 = o -> DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) >= configUtils.getBikeRange() && courier.getVehicle().compareTo(Vehicle.BICYCLE) != 0;
		assertFalse(in
			.stream()
			.filter(predicate2)
			.findFirst().isPresent()
			);
		
	}

	@Test
	final void testProcessRequest() {
		
		Courier courier = courierRepository.findById("courier-7e1552836a04");
		
		Map<Long, List<Order>> in = service.processRequest("courier-7e1552836a04");
		
		Long maxGroups = new Double(configUtils.getBikeRange()/configUtils.getDistanceSplitter()).longValue();
		
		assertFalse(in
				.keySet()
				.size() > maxGroups);
		
		Map<Long, List<Order>> expected = 
    			service.filterOrderList(courier)
    			.stream()
    			.sorted(service.organizeInnerSorting(courier)).parallel()
    			.collect(Collectors.groupingBy(o -> {
    				return new Double(DistanceCalculator.calculateDistance(o.getPickup(), courier.getLocation()) / configUtils.getDistanceSplitter()).longValue();	
    			}));
		
		assertEquals(expected.keySet().size(), in.keySet().size());
		for(Entry<Long, List<Order>> entry : in.entrySet()) {
			assertThat(entry.getValue(), is(in.get(entry.getKey())));
		}
		
	}

}
