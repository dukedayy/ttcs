package com.ttcs.ttcs_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private String id;
    private String productId;
    private String productName;
    private String mainImageUrl;
    private Float price;
    private Integer amountInCart;
    private Float subTotal;
}