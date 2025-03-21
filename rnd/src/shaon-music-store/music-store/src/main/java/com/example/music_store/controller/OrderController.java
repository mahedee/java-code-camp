package com.example.music_store.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.music_store.entity.Order;
import com.example.music_store.repository.OrderRepository;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create a new order
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        orderRepository.save(order);
        return ResponseEntity.ok("Order created successfully");
    }

    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get an order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        Order order = orderRepository.findById(id);
        return ResponseEntity.ok(order);
    }

    // Update an order
    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable int id, @RequestBody Order order) {
        order.setId(id);
        orderRepository.update(order);
        return ResponseEntity.ok("Order updated successfully");
    }

    // Delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable int id) {
        orderRepository.delete(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
