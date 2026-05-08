package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.model.Order;
import com.ttcs.ttcs_app.model.OrderStatus;
import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.repository.OrderRepository;
import com.ttcs.ttcs_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Page<Order> getAllOrdersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public void updateOrderStatus(String orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(OrderStatus.valueOf(newStatus));
        orderRepository.save(order);
    }

    @Transactional
    public void updateProductStock(String productId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }
}