package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
