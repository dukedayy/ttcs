package com.ttcs.ttcs_app.dto.request;

import com.ttcs.ttcs_app.model.VoucherStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoucherRequest {
    @NotBlank(message = "mã code không được để trống")
    private String voucherCode;

    @NotNull(message = "Không được để trống")
    private Boolean usePercentage;

    @Builder.Default
    private Float discountPercentage= 0.0F;
    @Builder.Default
    private Float discountValue = 0F;
    @Builder.Default
    private Integer minOrderValue = 0;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private Integer usageLimit=9999;
    @Builder.Default
    private Integer usedCount = 0;
    @Builder.Default
    private Integer limitPerUser = 0;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

}
