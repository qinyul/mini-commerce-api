package com.example.demo.service;

import java.util.UUID;

import com.example.demo.entity.Stock;

public interface StockService {

    Stock createInitialStock(UUID productExternalId, int initialQuantity);

    Stock increaseStock(UUID productExternalId, int amount);

    Stock decreaseStock(UUID productExternalId, int amount);
}
