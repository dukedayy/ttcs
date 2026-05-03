package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Customer;
import com.ttcs.ttcs_app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomer(Customer customer);
}
