package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
