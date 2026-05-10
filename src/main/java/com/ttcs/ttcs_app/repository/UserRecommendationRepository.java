package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, String> {
    // Tìm kết quả gợi ý theo ID của User
    Optional<UserRecommendation> findByUserId(String userId);
}