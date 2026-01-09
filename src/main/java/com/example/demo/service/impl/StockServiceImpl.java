package com.example.demo.service.impl;

import java.util.UUID;

import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.StockService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    public StockServiceImpl(
            StockRepository stockRepository,
            ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Stock createInitialStock(UUID productExternalId, int initialQuantity) {
        Product product = productRepository.findByExternalId(productExternalId).orElseThrow(
                () -> new EntityNotFoundException("Product not found: " + productExternalId));
        Stock stock = new Stock(product, initialQuantity);

        try {
            return stockRepository.save(stock);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Stock already exists for product: " + productExternalId);
        }

    }

    /* ===== Increase (Optimistic) ===== */
    @Override
    @Transactional
    public Stock increaseStock(UUID productExternalId, int amount) {

        try {
            Stock stock = stockRepository.findByProductExternalIdForUpdate(productExternalId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Stock not found for product: " + productExternalId));

            stock.increase(amount);
            return stock;
        } catch (OptimisticEntityLockException ex) {
            throw ex;
        }
    }

    /* ===== Decrease (Pessimistic) ===== */

    @Override
    @Transactional
    public Stock decreaseStock(UUID productExternalId, int amount) {

        Stock stock = stockRepository.findByProductExternalIdForUpdate(productExternalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Stock not found for product: " + productExternalId));

        stock.decrease(amount);
        return stock;
    }
}
