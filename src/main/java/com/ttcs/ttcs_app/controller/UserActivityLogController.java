package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.dto.request.UserActivityLogRequest;
import com.ttcs.ttcs_app.service.UserActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/activity-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserActivityLogController {

    private final UserActivityLogService logService;

    @PostMapping
    public ResponseEntity<Void> trackActivity(@RequestBody UserActivityLogRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logService.saveLog(email, request.getProductId(), request.getAction(), request.getStayTime());
        return ResponseEntity.ok().build();
    }
}