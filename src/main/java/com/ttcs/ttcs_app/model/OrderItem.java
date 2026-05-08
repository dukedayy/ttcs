package com.ttcs.ttcs_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orderItems", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String nameAtOrder;

    @Column(nullable = false)
    private Float priceAtOrder;

    @Column(nullable = false)
    private Float costPriceAtOrder;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Float subtotal;
}
