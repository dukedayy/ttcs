package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.model.ProductSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSimilarityRepository extends JpaRepository<ProductSimilarity, String> {

    List<ProductSimilarity> findTop10ByProductAOrderByScoreDesc(Product productA);

    void deleteByProductA(Product productA);

    // void deleteAllInBatch();
}