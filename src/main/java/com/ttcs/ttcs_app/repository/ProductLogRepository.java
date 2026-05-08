package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.ProductLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLogRepository extends JpaRepository<ProductLog, String> {
}