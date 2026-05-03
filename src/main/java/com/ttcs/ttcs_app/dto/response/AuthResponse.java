package com.ttcs.ttcs_app.dto.response; // Đổi package cho phù hợp

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;

    @Builder.Default // Gán mặc định luôn là Bearer
    private String tokenType = "Bearer";

    // Trả thêm chút thông tin cho Frontend xài
    private String email;
    private String role;
}