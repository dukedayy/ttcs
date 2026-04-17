package com.ttcs.ttcs_app.model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.time.LocalDateTime;

@Entity
@Table(name = "orderLogs", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private User user;

    private String action;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String comment;
    private LocalDateTime createdAt;
}
