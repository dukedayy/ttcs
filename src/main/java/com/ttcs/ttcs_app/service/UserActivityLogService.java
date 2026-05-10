package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.engine.RecommendationEngine;
import com.ttcs.ttcs_app.model.*;
import com.ttcs.ttcs_app.repository.ProductRepository;
import com.ttcs.ttcs_app.repository.UserActivityLogRepository;
import com.ttcs.ttcs_app.repository.UserRecommendationRepository;
import com.ttcs.ttcs_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityLogService {

    private final UserActivityLogRepository logRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final RecommendationEngine engine;
    private final UserRecommendationRepository userRecommendationRepository;

    @Async
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
            logRepository.save(logEntry);

        } catch (IllegalArgumentException e) {
            log.error("Action Type không hợp lệ: {}", actionStr);
        } catch (Exception e) {
            log.error("Lỗi khi ghi User Activity Log vào DB: ", e);
        }
    }

    public Map<String, Map<String, Double>> buildMatrix() {
        List<UserActivityLog> allLogs = logRepository.findAll();
        Map<String, Map<String, Double>> matrix = new HashMap<>();
        System.out.println(">>> CHECK: Số lượng log đọc được từ DB: " + allLogs.size());

        if (allLogs.isEmpty()) {
            System.out.println(">>> CẢNH BÁO: Bảng user_activity_logs đang trống rỗng!");
        }
        for (UserActivityLog log : allLogs) {
            String uId = log.getUser().getId();
            String pId = log.getProduct().getId();
            double points = calculatePoints(log);

            matrix.computeIfAbsent(uId, k -> new HashMap<>());
            // Cộng dồn điểm nếu user tương tác nhiều lần với 1 món
            matrix.get(uId).put(pId, matrix.get(uId).getOrDefault(pId, 0.0) + points);
        }
        return matrix;
    }

    public List<UserItemRating> preprocessData() {
        List<UserActivityLog> allLogs = logRepository.findAll();

        //Map<UserId + ":" + ProductId, TotalPoint>
        Map<String, Double> ratingMap = new HashMap<>();

        for (UserActivityLog log : allLogs) {
            String key = log.getUser().getId() + ":" + log.getProduct().getId();
            double points = calculatePoints(log);
            ratingMap.put(key, ratingMap.getOrDefault(key, 0.0) + points);
        }

        return ratingMap.entrySet().stream().map(entry -> {
            String[] split = entry.getKey().split(":");
            return new UserItemRating(split[0], split[1], entry.getValue());
        }).collect(Collectors.toList());
    }

    private double calculatePoints(UserActivityLog log) {
        switch (log.getAction()) {
            case BUY: return 5.0;
            case LIKE: return 3.5;
            case ADD_TO_CART: return 3.5;
            case VIEW:
//                return (log.getStayTime() != null && log.getStayTime() > 30) ? 1.5 : 1.0;
                if (log.getStayTime()<3) return 0.0;
                if (log.getStayTime()<10) return 1.0;
                if (log.getStayTime()<20) return 1.5;
                if (log.getStayTime()<30) return 2.0;
                return 2.5;
            default: return 0.0;
        }
    }

    public void pushToRecommendationEngine() {
        List<UserItemRating> processedData = preprocessData();
        String rsServiceUrl = "http://localhost:5000/train-model";
        restTemplate.postForEntity(rsServiceUrl, processedData, String.class);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void refreshAllRecommendations() {
        Map<String, Map<String, Double>> matrix = buildMatrix();
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            List<String> suggestedIds = engine.getUserBasedRecommendations(user.getId(), matrix);

            UserRecommendation rec = userRecommendationRepository.findByUserId(user.getId())
                    .orElse(new UserRecommendation(user.getId()));
            rec.setProductIds(String.join(",", suggestedIds));
            rec.setUpdatedAt(LocalDateTime.now());
            userRecommendationRepository.save(rec);
        }
    }
}