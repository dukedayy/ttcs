package com.ttcs.ttcs_app.config;

import com.ttcs.ttcs_app.model.Role;
import com.ttcs.ttcs_app.model.Status;
import com.ttcs.ttcs_app.model.User;
import com.ttcs.ttcs_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        String adminEmail = "dukeadmin@gmail.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            System.out.println("Chưa có Admin! Đang tiến hành tạo Super Admin...");

            User admin = User.builder()
                    .email(adminEmail)
                    .username("dukeadmin")
                    .password(passwordEncoder.encode("admin"))
                    .fullname("Trùm Cuối Hệ Thống")
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();

            userRepository.save(admin);
            System.out.println("Đã tạo Super Admin thành công! Email: " + adminEmail + " | Pass: admin");
        } else {
            System.out.println("Hệ thống đã có Admin, bỏ qua bước khởi tạo.");
        }
    }
}