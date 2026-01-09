package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.stock.CreateStockRequest;
import com.example.demo.dto.request.stock.StockAdjustmentRequest;
import com.example.demo.dto.response.stock.StockResponse;
import com.example.demo.entity.Stock;
import com.example.demo.service.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Inventory", description = "Stock management and inventory adjustments")
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(summary = "Initiliaze Stocks", description = "Sets the initial stock level for a product. Usually called once when onboarding a product.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stock created succeffully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or input"),
            @ApiResponse(responseCode = "404", description = "Product ID not found")
    })
    @PostMapping
    public ResponseEntity<StockResponse> create(
            @Valid @RequestBody CreateStockRequest request) {

        Stock stock = stockService.createInitialStock(request.productId(), request.initialQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(StockResponse.from(stock));
    }

    @Operation(summary = "Increase Stock (Restock)", description = "Adds inventory to an existing product. Thread-safe operation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock updated succeffully"),
            @ApiResponse(responseCode = "404", description = "Product/Stock not found")
    })
    @PostMapping("/{productId}/increase")
    public StockResponse increase(
            @PathVariable UUID productId,
            @Valid @RequestBody StockAdjustmentRequest request) {

        Stock stock = stockService.increaseStock(productId, request.amount());

        return StockResponse.from(stock);
    }

    @Operation(summary = "Decrease Stock (Restock)", description = "Reduces intentory. Throws error if insufficient stock available")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock deducted succeffully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount (negative or zero)"),
            @ApiResponse(responseCode = "409", description = "Conflict: Insufficient stock remaining"),
            @ApiResponse(responseCode = "404", description = "Product/Stock not found")
    })
    @PostMapping("/{productId}/decrease")
    public StockResponse decrease(
            @PathVariable UUID productId,
            @Valid @RequestBody StockAdjustmentRequest request) {

        Stock stock = stockService.decreaseStock(productId, request.amount());

        return StockResponse.from(stock);
    }
}
