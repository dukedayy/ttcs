    package com.ttcs.ttcs_app.dto.request;

    import com.ttcs.ttcs_app.model.ProductStatus;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Enumerated;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.AllArgsConstructor;
    import lombok.Data;

    @Data
    @AllArgsConstructor
    public class CreateProductRequest {
        @NotBlank(message = "Tên sản phẩm không được để trống")
        private String name;

        @NotNull(message = "Giá sản phẩm không được để trống")
        private Float price;

        private Float costPrice;
        private String description;
        private Integer stockQuantity;
        private Float weight;
        private String sizes;
        private Float taxRate;
        private String mainImageUrl;

        @Enumerated(EnumType.STRING)
        private ProductStatus status;

        private String categoryId;
    }
