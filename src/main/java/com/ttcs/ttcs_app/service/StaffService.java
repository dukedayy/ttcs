package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.response.OrderResponse;
import com.ttcs.ttcs_app.model.Order;
import com.ttcs.ttcs_app.model.OrderStatus;
import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.repository.OrderRepository;
import com.ttcs.ttcs_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Page<OrderResponse> getAllOrdersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::convertToOrderResponse);
    }

    private OrderResponse convertToOrderResponse(Order order) {

        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : "Sản phẩm không tồn tại")
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtOrder())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .shippingAddress(order.getShippingAddress())
                .trackingNumber(order.getTrackingNumber())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .customerId(order.getCustomer() != null ? order.getCustomer().getUser().getId() : null)
                .staffId(order.getUser() != null ? order.getUser().getId() : null)
                .voucherId(order.getVoucher() != null ? order.getVoucher().getId() : null)
                .items(itemResponses)
                .build();
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