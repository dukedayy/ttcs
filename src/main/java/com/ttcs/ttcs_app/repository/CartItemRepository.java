package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Cart;
import com.ttcs.ttcs_app.model.CartItem;
import com.ttcs.ttcs_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    List<CartItem> findByCart(Cart cart);
}
