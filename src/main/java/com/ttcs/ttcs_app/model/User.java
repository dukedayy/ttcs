//package com.ttcs.ttcs_app.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Entity
//@Table(name = "users", schema = "public")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
////    @Column(name = "id")
//    private String id;
//
//    @Column(nullable = false, unique = true)
//    private String username;
//
//    private String fullname;
//
//    @Column(unique = true)
//    private String email;
//
//    @Column(unique = true)
//    private String phoneNumber;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    @Enumerated(EnumType.STRING)
//    private Status status;
//
//    private String avatarUrl;
//
//    @Enumerated(EnumType.STRING)
//    private Gender gender;
//
//    private LocalDate lastLogin;
//
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Customer customer;
//
//    @Builder.Default
//    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Conversation> conversationAsUser1 = new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Conversation> conversationAsUser2 = new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Order> userOrder = new ArrayList<>();
//}
package com.ttcs.ttcs_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    private String fullname;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate lastLogin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer customer;

    @Builder.Default
    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Conversation> conversationAsUser1 = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Conversation> conversationAsUser2 = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> userOrder = new ArrayList<>();
}