package com.ttcs.ttcs_app.dto.request;

import lombok.Data;

@Data
public class ProductLogRequest {
    private String productId;
    private String actionType; // VIEW, ADD_TO_CART, BUY, LIKE.
    private Float duration;
}
