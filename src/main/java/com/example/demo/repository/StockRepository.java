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

import com.example.demo.entity.Stock;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

        @Query("""
                        select s
                        from Stock s
                        join s.product p
                        where p.externalId = :productExternalId
                        """)
        Optional<Stock> findByProductExternalId(
                        @Param("productExternalId") UUID productExternalId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") }) // Wait max 3 seconds
        @Query("""
                        select s
                        from Stock s
                        join s.product p
                        where p.externalId = :productExternalId
                        """)
        Optional<Stock> findByProductExternalIdForUpdate(
                        @Param("productExternalId") UUID productExternalId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") })
        @Query("""
                        select s
                        from Stock s
                        Join s.product p
                        where p.externalId in :productIds
                        """)
        List<Stock> findAllByProductIdsForUpdate(@Param("productIds") List<UUID> productIds);
}
