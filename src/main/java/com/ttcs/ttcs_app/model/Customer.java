//package com.ttcs.ttcs_app.model;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "customers", schema = "public")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Customer {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false, unique = true)
//    private User user;
//
//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
//    private Cart cart;
//
//    private Integer loyaltyPoint;
//    private String address;
//
//
//}
package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // Phía chủ sở hữu (Owning side) của Cart
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cartId", referencedColumnName = "id")
    private Cart cart;

    private Integer loyaltyPoint;
    private String address;
}