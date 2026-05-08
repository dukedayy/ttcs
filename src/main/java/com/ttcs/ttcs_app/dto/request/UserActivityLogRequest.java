package com.ttcs.ttcs_app.dto.request;

import lombok.Data;

@Data
public class UserActivityLogRequest {
    private String productId;
    private String action;
    private Float stayTime;
}