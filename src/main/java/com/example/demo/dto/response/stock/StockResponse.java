package com.example.demo.dto.response.stock;

import java.util.UUID;

import com.example.demo.entity.Stock;

public record StockResponse(
                UUID productId,
                int quantity) {

        public static StockResponse from(Stock stock) {
                return new StockResponse(
                                stock.getProduct().getExternalId(),
                                stock.getQuantity());
        }
}
