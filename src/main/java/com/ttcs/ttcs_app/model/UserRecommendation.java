package com.ttcs.ttcs_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_recommendations")
@Getter
@Setter
@NoArgsConstructor
public class UserRecommendation {
    @Id
    private String userId;

    @Column(columnDefinition = "TEXT")
    private String productIds;

    private LocalDateTime updatedAt;

    public UserRecommendation(String userId) { this.userId = userId; }
}