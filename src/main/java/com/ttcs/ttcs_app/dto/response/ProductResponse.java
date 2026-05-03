package com.ttcs.ttcs_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private Float price;
    private String description;
    private String mainImageUrl;
    private String imagesUrl;
    private Integer stockQuantity;
    private Float weight;
    private String sizes;
    private String categoryId;
    private Float averageScore;
}
