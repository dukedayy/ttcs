package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.request.CreateStaffAccountRequest;
import com.ttcs.ttcs_app.dto.request.LoginRequest;
import com.ttcs.ttcs_app.dto.request.RegisterRequest;
import com.ttcs.ttcs_app.dto.response.AuthResponse;
import com.ttcs.ttcs_app.model.*;
import com.ttcs.ttcs_app.repository.CartRepository;
import com.ttcs.ttcs_app.repository.CustomerRepository;
import com.ttcs.ttcs_app.repository.UserRepository;
import com.ttcs.ttcs_app.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;

    @Transactional
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        User user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullname(request.getFullname())
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        Customer customer = Customer.builder()
                .user(savedUser)
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        Cart cart = Cart.builder()
                .customer(savedCustomer)
                .build();
        cartRepository.save(cart);

        return "Đăng ký tài khoản thành công!";
    }

    @Transactional
    public String createAccountForStaff(CreateStaffAccountRequest request){
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email này đã được sử dụng!");
        User user = User.builder().username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullname(request.getFullname())
                .role(request.getRole().equals("Staff") ? Role.STAFF : Role.SALES_STAFF)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);
        return "Đăng ký tài khoản " + request.getRole() + " thành công!";
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy User"));

        String jwtToken = jwtUtils.generateToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();
    }


}