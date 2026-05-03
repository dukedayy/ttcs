package com.ttcs.ttcs_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotNull(message = "Giá không được để trống")
    private Float price;

    private Float costPrice;
    private Integer stockQuantity;
    private Float weight;
    private String sizes;
    private Float taxRate;
    private String mainImageUrl;
    private String description;

    @NotBlank(message = "ID Danh mục không được để trống")
    private String categoryId;
}