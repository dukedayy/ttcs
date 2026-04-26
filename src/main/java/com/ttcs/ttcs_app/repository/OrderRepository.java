package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
