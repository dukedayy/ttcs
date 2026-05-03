package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.request.OrderRequest;
import com.ttcs.ttcs_app.dto.response.OrderResponse;
import com.ttcs.ttcs_app.exception.ResourceNotFoundException;
import com.ttcs.ttcs_app.model.*;
import com.ttcs.ttcs_app.repository.OrderRepository;
import com.ttcs.ttcs_app.repository.ProductRepository;
import com.ttcs.ttcs_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Customer customer = currentUser.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Hồ sơ Customer không tồn tại");
        }

        Order order = Order.builder()
                .customer(customer)
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress() + " | " + request.getReceiverName() + " | " + request.getReceiverPhone())
                .note(request.getNote())
                .orderItems(new ArrayList<>())
                .build();

        float totalAmount = 0f;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng tồn kho!");
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            Float subtotal = product.getPrice() * itemRequest.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .nameAtOrder(product.getName())
                    .priceAtOrder(product.getPrice())
                    .costPriceAtOrder(product.getCostPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getOrderItems().add(orderItem);
            totalAmount += subtotal;
        }

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount);
        order.setShippingFee(0f);
        order.setDiscountAmount(0f);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getNameAtOrder())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtOrder())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .shippingAddress(order.getShippingAddress())
                .trackingNumber(order.getTrackingNumber())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }

    @Transactional
    public List<OrderResponse> getOrders(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Customer customer = currentUser.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Hồ sơ Customer không tồn tại");
        }
        List<Order> orders = orderRepository.findByCustomer(customer);
        List<OrderResponse> orderResponseList =  orders.stream()
                .map(this::mapToResponse)
                .toList();
        return orderResponseList;
    }

    @Transactional
    public OrderResponse getOrder(String orderId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Customer customer = currentUser.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Hồ sơ Customer không tồn tại");
        }
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) throw new ResourceNotFoundException("Không tìm thấy order này hoặc bạn không sở hữu order này!");
        else return mapToResponse(order.get());
    }

    @Transactional
    public String cancelOrder(String orderId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy đơn hàng!"));

        if (!order.getCustomer().getId().equals(currentUser.getCustomer().getId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng của người khác!");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ xử lý!");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return "Hủy đơn hàng thành công và đã hoàn trả tồn kho!";
    }

}