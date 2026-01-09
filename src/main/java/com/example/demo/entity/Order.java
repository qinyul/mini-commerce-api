package com.example.demo.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.entity.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_external_id", columnList = "external_id"),
        @Index(name = "idx_orders_status", columnList = "status"),
})
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, updatable = false, unique = true)
    private UUID externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Version
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Order() {
    }

    public Order(List<OrderItem> items) {
        this.externalId = UUID.randomUUID();
        this.status = OrderStatus.CREATED;
        this.items.addAll(items);
        items.forEach(i -> i.attach(this));
        this.createdAt = Instant.now();
    }

    public void confirm() {
        ensureStatus(OrderStatus.CREATED);
        this.status = OrderStatus.CONFIRMED;
    }

    public void ship() {
        ensureStatus(OrderStatus.CONFIRMED);
        this.status = OrderStatus.SHIPPED;
    }

    public void complete() {
        ensureStatus(OrderStatus.SHIPPED);
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Completed orders cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    private void ensureStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Invalid state transition from " + status);
        }
    }
}
