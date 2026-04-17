package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cartItems", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId", referencedColumnName = "id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer amountInCart;
}
