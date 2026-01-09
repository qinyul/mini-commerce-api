package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByExternalId(UUID externalId);

    @EntityGraph(attributePaths = { "items", "items.product" })
    Optional<Order> findWithItemsByExternalId(UUID externalUuid);
}
