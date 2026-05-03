package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Cart;
import com.ttcs.ttcs_app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByCustomer(Customer customer);
}
