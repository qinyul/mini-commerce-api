package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "stocks", uniqueConstraints = {
        @UniqueConstraint(name = "uk_stock_product", columnNames = "product_id")
})

@Getter
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    protected Stock() {
    }

    public Stock(Product product, Integer initialQuantity) {
        this.product = product;
        this.quantity = initialQuantity;
    }

    public void increase(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Increase amount must be positive");
        }
        this.quantity += amount;
    }

    public void decrease(int amount) {
        if (this.quantity < amount) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.quantity -= amount;
    }
}