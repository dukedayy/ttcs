package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.model.ProductLike;
import com.ttcs.ttcs_app.model.ProductLikeId;
import com.ttcs.ttcs_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, ProductLikeId> {
    Optional<ProductLike> findByUserAndProduct(User user, Product product);
}