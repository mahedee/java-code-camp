package com.example.music_store.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.music_store.entity.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderRepository {
    
    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Insert a new order
    public int save(Order order) {
        String sql = "INSERT INTO orders (id, customer_name, total_amount) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, order.getId(), order.getCustomerName(), order.getTotalAmount());
    }

    // Fetch all orders
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        return jdbcTemplate.query(sql, new OrderRowMapper());
    }

    // Fetch a single order by ID
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new OrderRowMapper(), id);
    }

    // Update an order
    public int update(Order order) {
        String sql = "UPDATE orders SET customer_name = ?, total_amount = ? WHERE id = ?";
        return jdbcTemplate.update(sql, order.getCustomerName(), order.getTotalAmount(), order.getId());
    }

    // Delete an order by ID
    public int delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // RowMapper for Order
    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Order(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getDouble("total_amount")
            );
        }
    }
}