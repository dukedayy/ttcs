package com.ttcs.ttcs_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;
    private String customerId;
    private String staffId;
    private String voucherId;

    private String orderCode;
    private String status;

    private Float totalAmount;
    private Float shippingFee;
    private Float discountAmount;
    private Float finalAmount;

    private String shippingAddress;
    private String trackingNumber;
    private String note;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private String id;
        private String productId;
        private String productName;
        private Integer quantity;
        private Float price;
    }

}