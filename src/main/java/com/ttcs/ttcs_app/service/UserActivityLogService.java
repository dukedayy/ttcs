package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.model.User;
import com.ttcs.ttcs_app.model.UserActionType;
import com.ttcs.ttcs_app.model.UserActivityLog;
import com.ttcs.ttcs_app.repository.ProductRepository;
import com.ttcs.ttcs_app.repository.UserActivityLogRepository;
import com.ttcs.ttcs_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityLogService {

    private final UserActivityLogRepository logRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Async // Đẩy task này sang luồng khác để chạy ngầm
    @Transactional
    public void saveLog(String email, String productId, String actionStr, Float stayTime) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            Product product = productRepository.findById(productId).orElse(null);

            if (user == null || product == null) return;

            UserActionType actionType = UserActionType.valueOf(actionStr.toUpperCase());

            UserActivityLog logEntry = UserActivityLog.builder()
                    .user(user)
                    .product(product)
                    .action(actionType)
                    .stayTime(stayTime != null ? stayTime : 0f)
                    .build();
            // createdAt đã được lo bởi @PrePersist trong Entity của sếp

            logRepository.save(logEntry);

        } catch (IllegalArgumentException e) {
            log.error("Action Type không hợp lệ: {}", actionStr);
        } catch (Exception e) {
            log.error("Lỗi khi ghi User Activity Log vào DB: ", e);
        }
    }
}