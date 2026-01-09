package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByExternalId(UUID externalId);

    boolean existsByCode(String code);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Product p where p.externalId = :externalId")
    Optional<Product> findForUpdate(@Param("externalId") UUID externalId);

    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") })
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.externalId IN :ids")
    List<Product> findAllByIdsWithLock(@Param("ids") List<UUID> ids);
}
