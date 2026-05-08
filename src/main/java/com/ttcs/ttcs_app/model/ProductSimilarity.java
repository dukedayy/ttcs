package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "productSimilarities", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productAId", referencedColumnName = "id", nullable = false)
    private Product productA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productBId", referencedColumnName = "id", nullable = false)
    private Product productB;

    @Column(nullable = false)
    private Double score;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}