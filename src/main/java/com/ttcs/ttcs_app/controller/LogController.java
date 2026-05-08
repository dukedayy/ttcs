//package com.ttcs.ttcs_app.controller;
//
//import com.ttcs.ttcs_app.dto.request.ProductLogRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/customer/logs")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
//public class LogController {
//    private final LogService logService; // Sếp tự tạo Service để save vào bảng product_log nhé
//
//    @PostMapping
//    public ResponseEntity<Void> trackLog(@RequestBody ProductLogRequest request) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        // Bắn vào DB (Nên dùng @Async ở Service để không làm chậm request của User)
//        logService.saveLog(email, request.getProductId(), request.getActionType());
//        return ResponseEntity.ok().build();
//    }
//}