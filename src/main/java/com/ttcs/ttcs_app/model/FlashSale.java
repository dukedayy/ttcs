package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flashSales", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flashSale", orphanRemoval = true)
    private List<FlashSaleItem> flashSaleItemList = new ArrayList<>();
}
