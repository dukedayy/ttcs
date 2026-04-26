package com.ttcs.ttcs_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CategoryRenameRequestDTO {
    @NotBlank(message = "Tên mới không được để trống")
    private String newName;
}
