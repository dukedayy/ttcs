package com.ttcs.ttcs_app.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateFlashSaleRequest {

    @NotNull(message = "Không được để trống!")
    @FutureOrPresent(message = "Ngày bắt đầu phải từ hôm nay trở đi")
    private LocalDateTime startTime;

    @NotNull(message = "Không được để trống!")
    @Future(message = "Ngày kết thúc phải nằm trong tương lai")
    private LocalDateTime endTime;

    private String status= "UNACTIVE";
}
