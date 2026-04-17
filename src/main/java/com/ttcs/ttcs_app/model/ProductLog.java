package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "productLogs", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ProductStatus newStatus;

    private LocalDateTime createdAt;
}
