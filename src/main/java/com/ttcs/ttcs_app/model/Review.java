package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "id", nullable = false, unique = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "id", nullable = false, unique = true)
    private Customer customer;

    @Column(nullable = false)
    private Integer rate;
}
