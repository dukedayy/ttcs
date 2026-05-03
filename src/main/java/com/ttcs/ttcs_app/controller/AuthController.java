package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.dto.request.LoginRequest;
import com.ttcs.ttcs_app.dto.request.RegisterRequest;
import com.ttcs.ttcs_app.dto.response.ApiResponse;
import com.ttcs.ttcs_app.dto.response.AuthResponse;
import com.ttcs.ttcs_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/customer-register")
    public ResponseEntity<ApiResponse> customerRegister(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.ok(new ApiResponse("Đăng ký thành công!"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
