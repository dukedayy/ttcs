package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flashSaleItems", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FlashSaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashSaleId", referencedColumnName = "id", nullable = false)
    private FlashSale flashSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Float newPrice;

    @Column(nullable = false)
    private Integer limitPerUser;
}
