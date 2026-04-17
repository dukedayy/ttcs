//package com.ttcs.ttcs_app.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "carts", schema = "public")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//
//public class Cart {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private String id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customerId", referencedColumnName = "id", unique = true)
//    private Customer customer;
//
//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart", orphanRemoval = true)
//    private List<CartItem> cartItems = new ArrayList<>();
//}
package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // mappedBy trỏ về tên biến 'cart' bên Customer.java
    @OneToOne(mappedBy = "cart", fetch = FetchType.LAZY)
    private Customer customer;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
}