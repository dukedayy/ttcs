package com.ttcs.ttcs_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserItemRating {
    private String userId;
    private String productId;
    private Double rating;
}