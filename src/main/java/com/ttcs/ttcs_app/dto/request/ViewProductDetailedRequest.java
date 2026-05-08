package com.ttcs.ttcs_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewProductDetailedRequest {
    @NotBlank(message = "ProductId không được để trống!")
    private String productId;
}
