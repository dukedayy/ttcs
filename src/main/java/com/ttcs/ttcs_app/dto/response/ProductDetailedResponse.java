package com.ttcs.ttcs_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailedResponse {
    private String id;
    private String name;

    // Giá và Khuyến mãi
    private Float price; // Giá gốc
    private boolean isOnFlashSale;
    private Float flashSalePrice; // Trả về null nếu không có flash sale

    // Phân loại & Kho
    private String categoryId;
    private String categoryName; // Để Frontend làm Breadcrumb (VD: Trang chủ > Thời trang nam > Áo thun)
    private Integer stockQuantity;
    private String sizes;
    private Float weight;

    // Hình ảnh & Mô tả
    private String mainImageUrl;
    private String imagesUrl;
    private String description;

    // Tương tác & Đánh giá
    private Float averageScore;
    private Integer reviewCount; // Số lượng đánh giá
    private Integer likeCounts;  // Đếm lượt yêu thích
    private Integer views;       // Lượt xem
}