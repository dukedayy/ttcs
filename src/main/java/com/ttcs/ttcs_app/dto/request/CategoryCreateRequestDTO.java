package com.ttcs.ttcs_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequestDTO {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
}
